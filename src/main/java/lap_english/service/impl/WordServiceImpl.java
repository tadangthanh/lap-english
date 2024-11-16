package lap_english.service.impl;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import jakarta.transaction.Transactional;
import lap_english.dto.SubTopicDto;
import lap_english.dto.WordDto;
import lap_english.dto.response.BlobResponse;
import lap_english.dto.response.PageResponse;
import lap_english.entity.SubTopic;
import lap_english.entity.Word;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.WordMapper;
import lap_english.repository.SubTopicRepo;
import lap_english.repository.WordRepo;
import lap_english.service.IAzureService;
import lap_english.service.IWordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WordServiceImpl implements IWordService {
    private final WordMapper wordMapper;
    private final WordRepo wordRepo;
    private final SubTopicRepo subTopicRepo;
    private final IAzureService azureService;
    private final TextToSpeechService textToSpeechService;

    @Override
    public void delete(Long id) {
        wordRepo.deleteById(id);
    }

    @Override
    public WordDto create(WordDto dto) {
        Word word = wordMapper.toEntity(dto);
        SubTopic subTopic = getSubTopic(dto.getSubTopicId());
        word.setSubTopic(subTopic);
        word = wordRepo.saveAndFlush(word);
        uploadAudio(word, "en-GB", SsmlVoiceGender.FEMALE);
        uploadAudio(word, "en-US", SsmlVoiceGender.MALE);
        uploadImage(dto.getFile(), word);
        return wordMapper.toDto(word);
    }

    private void uploadImage(MultipartFile file, Word word) {
        if (file == null || !Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            return;
        }
        word.setImageBlobName(azureService.upload(file));
    }

    void deleteFile(String blobName) {
        if (blobName != null && !blobName.isEmpty()) {
            azureService.deleteBlob(blobName);
        }
    }

    private void uploadAudio(Word word, String languageCode, SsmlVoiceGender gender) {
        try {
            // Tạo âm thanh từ text
            byte[] audioData = textToSpeechService.synthesizeText(word.getWord(), languageCode, gender);
            // Upload âm thanh lên Azure Blob Storage
            InputStream inputStream = new ByteArrayInputStream(audioData);
            String fileName = "audio-" + System.currentTimeMillis() + ".mp3";
            String blobName = azureService.upload(inputStream, audioData.length, fileName, "audio/mp3");
            if (languageCode.equals("en-GB")) {
                word.setAudioUkBlobName(blobName);
            } else {
                word.setAudioUsBlobName(blobName);
            }

        } catch (Exception e) {
            log.error("Error when upload audio: {}", e.getMessage());
        }
    }

    private SubTopic getSubTopic(Long subTopicId) {
        return subTopicRepo.findById(subTopicId).orElseThrow(() -> {
            log.error("SubTopic not found");
            return new ResourceNotFoundException("SubTopic not found");
        });
    }

    @Override
    public WordDto update(WordDto dto) {
        Word wordExist = wordRepo.findById(dto.getId()).orElseThrow(() -> {
            log.error("Word not found");
            return new ResourceNotFoundException("Word not found");
        });
        wordMapper.updateEntityFromDto(dto, wordExist);
        return wordMapper.toDto(wordRepo.save(wordExist));
    }

    @Override
    public PageResponse<?> getBySubTopicId(Long subTopicId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Word> wordPage = wordRepo.findBySubTopicId(subTopicId, pageRequest);
        List<WordDto> wordDtoList = wordMapper.toListDto(wordPage.getContent());
        return PageResponse.builder()
                .items(wordDtoList)
                .totalItems(wordPage.getTotalElements())
                .totalPage(wordPage.getTotalPages())
                .hasNext(wordPage.hasNext())
                .pageNo(page)
                .pageSize(size).build();
    }
}
