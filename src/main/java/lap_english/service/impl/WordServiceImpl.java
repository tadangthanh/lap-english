package lap_english.service.impl;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import jakarta.transaction.Transactional;
import lap_english.dto.WordDto;
import lap_english.dto.response.PageResponse;
import lap_english.dto.response.ResponseData;
import lap_english.entity.SubTopic;
import lap_english.entity.Word;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.WordMapper;
import lap_english.repository.SubTopicRepo;
import lap_english.repository.UserRepo;
import lap_english.repository.WordRepo;
import lap_english.repository.specification.EntitySpecificationsBuilder;
import lap_english.service.IAzureService;
import lap_english.service.IImportWordExcelService;
import lap_english.service.IWordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

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
    private final IImportWordExcelService importWordExcelService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepo userRepo;

    @Override
    public void delete(Long id) {
        Word wordExisting = wordRepo.findById(id).orElseThrow(() -> {
            log.error("Word not found");
            return new ResourceNotFoundException("Word not found");
        });
        List<String> blobNames = getListBlobWord(List.of(wordExisting));
        wordRepo.deleteById(id);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_COMMITTED) {
                    blobNames.forEach(blobName -> deleteFileByBlobName(blobName));
                }
            }
        });
    }

    private List<String> getListBlobWord(List<Word> words) {
        List<String> blobNames = new ArrayList<>();
        for (Word word : words) {
            if (word.getAudioUkBlobName() != null) {
                blobNames.add(word.getAudioUkBlobName());
            }
            if (word.getAudioUsBlobName() != null) {
                blobNames.add(word.getAudioUsBlobName());
            }
            if (word.getImageBlobName() != null) {
                blobNames.add(word.getImageBlobName());
            }
        }
        return blobNames;
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

    void deleteFileByBlobName(String blobName) {
        if (blobName != null && !blobName.isEmpty()) {
            azureService.deleteBlob(blobName);
        }
    }

    private void uploadDoubleAudioUsUk(Word word) {
        uploadAudio(word, "en-GB", SsmlVoiceGender.FEMALE);
        uploadAudio(word, "en-US", SsmlVoiceGender.MALE);
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
        List<String> blobNames = getListBlobWord(List.of(wordExist));

        wordMapper.updateEntityFromDto(dto, wordExist);
        wordExist = wordRepo.save(wordExist);
        Word finalWordExist = wordExist;
        // xóa file cũ và upload file mới sau khi commit transaction
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                blobNames.forEach(blobName -> deleteFileByBlobName(blobName));
                uploadDoubleAudioUsUk(finalWordExist);
            }

            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) {
                    // Thực hiện hành động khác nếu transaction không thành công
                    log.error("Transaction rolled back, no file deletion.");
                }
            }
        });
        return wordMapper.toDto(wordRepo.save(finalWordExist));
    }

    @Override
    public PageResponse<?> getBySubTopicId(Long subTopicId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Word> wordPage = wordRepo.findBySubTopicId(subTopicId, pageRequest);
        List<WordDto> wordDtoList = wordMapper.toListDto(wordPage.getContent());
        return PageResponse.builder().items(wordDtoList).totalItems(wordPage.getTotalElements()).totalPage(wordPage.getTotalPages()).hasNext(wordPage.hasNext()).pageNo(page).pageSize(size).build();
    }

    @Override
    public PageResponse<?> advanceSearchBySpecification(Pageable pageable, String[] word) {
        log.info("request get all of word with specification");
        if (word != null && word.length > 0) {
            EntitySpecificationsBuilder<Word> builder = new EntitySpecificationsBuilder<>();
//            Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
            Pattern pattern = Pattern.compile("([a-zA-Z0-9_.]+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
            //patten chia ra thành 5 nhóm
            // nhóm 1: từ cần tìm kiếm (có thể là tên cột hoặc tên bảng) , ví dụ: name, age, subTopic.id=> subTopic là tên bảng, id là tên cột
            // nhóm 2: toán tử tìm kiếm
            // nhóm 3: giá trị cần tìm kiếm
            // nhóm 4: dấu câu cuối cùng
            // nhóm 5: dấu câu cuối cùng
            for (String s : word) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }
            Specification<Word> spec = builder.build();
            // nó trả trả về 1 spec mới
//            spec=spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("subTopic").get("id"), subTopicId));
            Page<Word> wordPage = wordRepo.findAll(spec, pageable);

            return convertToPageResponse(wordPage, pageable);
        }
        return convertToPageResponse(wordRepo.findAll(pageable), pageable);
    }

    @Override
    public void deleteBySubTopicId(Long subTopicId) {
        List<Word> words = wordRepo.findAllBySubTopicId(subTopicId);
        // Lấy ra tên của các file blob, sau khi commit thì xóa các file blob
        List<String> blobNames = getListBlobWord(words);
        wordRepo.deleteAll(words);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_COMMITTED) {
                    blobNames.forEach(blobName -> deleteFileByBlobName(blobName));
                }
            }
        });
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<Integer> importFromExcel(Long subTopicId, MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            List<WordDto> wordList = importWordExcelService.importWordExcel(file);
            for (WordDto wordDto : wordList) {
                wordDto.setSubTopicId(subTopicId);
                create(wordDto);
            }
            // Gửi thông báo khi hoàn thành
            messagingTemplate.convertAndSendToUser(username, "/topic/import-status",
                    new ResponseData<>(HttpStatus.CREATED.value(), "import successful", wordList.size()));
            return CompletableFuture.completedFuture(wordList.size());
        } catch (Exception e) {
            log.error("Fail to import word: {}", e.getMessage());
            messagingTemplate.convertAndSendToUser(username, "/topic/import-status",
                    new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
            return CompletableFuture.failedFuture(e);
        }
    }


    private PageResponse<?> convertToPageResponse(Page<Word> wordPage, Pageable pageable) {
        List<WordDto> response = wordPage.stream().map(this.wordMapper::toDto).collect(toList());
        return PageResponse.builder().items(response).totalItems(wordPage.getTotalElements()).totalPage(wordPage.getTotalPages()).hasNext(wordPage.hasNext()).pageNo(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
    }
}
