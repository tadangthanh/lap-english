package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.SentenceDto;
import lap_english.dto.SubTopicDto;
import lap_english.dto.response.PageResponse;
import lap_english.entity.Sentence;
import lap_english.entity.SubTopic;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.SentenceMapper;
import lap_english.repository.SentenceRepo;
import lap_english.repository.SubTopicRepo;
import lap_english.service.ISentenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SentenceServiceImpl implements ISentenceService {
    private final SentenceMapper sentenceMapper;
    private final SentenceRepo sentenceRepo;
    private final SubTopicRepo subTopicRepo;

    @Override
    public void delete(Long id) {
        sentenceRepo.deleteById(id);
    }

    @Override
    public SentenceDto save(SentenceDto sentenceDto) {
        Sentence sentence = sentenceMapper.toEntity(sentenceDto);
        SubTopic subTopic = getSubTopic(sentenceDto.getSubTopicId());
        sentence.setSubTopic(subTopic);
        sentenceRepo.save(sentence);
        return sentenceMapper.toDto(sentence);
    }

    private SubTopic getSubTopic(Long subTopicId) {
        return subTopicRepo.findById(subTopicId).orElseThrow(() -> {
            log.error("Sub topic not found");
            return new ResourceNotFoundException("Sub topic not found");
        });
    }


    @Override
    public PageResponse<?> getAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Sentence> sentencePage = sentenceRepo.findAll(pageRequest);
        List<SentenceDto> sentenceDtoList = sentenceMapper.toListDto(sentencePage.getContent());
        return PageResponse.builder().items(sentenceDtoList).totalItems(sentencePage.getTotalElements()).totalPage(sentencePage.getTotalPages()).hasNext(sentencePage.hasNext()).pageNo(page).pageSize(size).build();
    }

    @Override
    public SentenceDto findById(Long id) {
        Sentence sentence = sentenceRepo.findById(id).orElseThrow(() -> {
            log.error("Sentence not found");
            return new ResourceNotFoundException("Sentence not found");
        });
        return sentenceMapper.toDto(sentence);
    }

    @Override
    public SentenceDto update(SentenceDto sentenceDto) {
        Sentence sentenceExist = sentenceRepo.findById(sentenceDto.getId()).orElseThrow(() -> {
            log.error("Sentence not found");
            return new ResourceNotFoundException("Sentence not found");
        });
        sentenceMapper.updateFromDto(sentenceDto, sentenceExist);
        return sentenceMapper.toDto(sentenceRepo.save(sentenceExist));
    }

    @Override
    public PageResponse<?> getBySubTopicId(Long subTopicId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Sentence> sentencePage = sentenceRepo.findBySubTopicId(subTopicId, pageRequest);
        List<SentenceDto> sentenceDtoList = sentenceMapper.toListDto(sentencePage.getContent());
        return PageResponse.builder().items(sentenceDtoList).totalItems(sentencePage.getTotalElements()).totalPage(sentencePage.getTotalPages()).hasNext(sentencePage.hasNext()).pageNo(page).pageSize(size).build();
    }
}
