package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.SubTopicDto;
import lap_english.dto.WordDto;
import lap_english.dto.response.PageResponse;
import lap_english.entity.SubTopic;
import lap_english.entity.Word;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.WordMapper;
import lap_english.repository.SubTopicRepo;
import lap_english.repository.WordRepo;
import lap_english.service.IWordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WordServiceImpl implements IWordService {
    private final WordMapper wordMapper;
    private final WordRepo wordRepo;
    private final SubTopicRepo subTopicRepo;

    @Override
    public void delete(Long id) {
        wordRepo.deleteById(id);
    }

    @Override
    public WordDto create(WordDto dto) {
        Word word = wordMapper.toEntity(dto);
        SubTopic subTopic = getSubTopic(dto.getSubTopicId());
        word.setSubTopic(subTopic);
        wordRepo.save(word);
        return wordMapper.toDto(word);
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
