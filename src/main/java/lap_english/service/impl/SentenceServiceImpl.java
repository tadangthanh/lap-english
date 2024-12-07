package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.SentenceDto;
import lap_english.dto.response.PageResponse;
import lap_english.dto.response.ResponseData;
import lap_english.entity.Sentence;
import lap_english.entity.SubTopic;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.SentenceMapper;
import lap_english.repository.SentenceRepo;
import lap_english.repository.SubTopicRepo;
import lap_english.repository.specification.EntitySpecificationsBuilder;
import lap_english.service.IReaderSentenceExcelService;
import lap_english.service.ISentenceService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SentenceServiceImpl implements ISentenceService {
    private final SentenceMapper sentenceMapper;
    private final SentenceRepo sentenceRepo;
    private final SubTopicRepo subTopicRepo;
    private final IReaderSentenceExcelService readerSentenceExcelService;
    private final SimpMessagingTemplate messagingTemplate;

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

    @Override
    public PageResponse<?> advancedSearch(Pageable pageable, String[] sentence) {
        if (sentence != null && sentence.length > 0) {
            EntitySpecificationsBuilder<Sentence> builder = new EntitySpecificationsBuilder<>();
            Pattern pattern = Pattern.compile("([a-zA-Z0-9_.]+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
            for (String s : sentence) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }
            Specification<Sentence> spec = builder.build();
            // nó trả trả về 1 spec mới
            Page<Sentence> sentencePage = sentenceRepo.findAll(spec, pageable);

            return convertToPageResponse(sentencePage, pageable);
        }
        return convertToPageResponse(sentenceRepo.findAll(pageable), pageable);
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<Integer> importFromExcel(Long subTopicId, MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            List<SentenceDto> sentenceDtoList = readerSentenceExcelService.importSentenceFromExcel(file);
            for (SentenceDto sentenceDto : sentenceDtoList) {
                sentenceDto.setSubTopicId(subTopicId);
                save(sentenceDto);
            }
            // Gửi thông báo khi hoàn thành
            messagingTemplate.convertAndSendToUser(username, "/topic/import-sentence-status",
                    new ResponseData<>(HttpStatus.CREATED.value(), "import successful", sentenceDtoList.size()));
            return CompletableFuture.completedFuture(sentenceDtoList.size());
        } catch (Exception e) {
            log.error("Fail to import word: {}", e.getMessage());
            messagingTemplate.convertAndSendToUser(username, "/topic/import-sentence-status",
                    new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
            return CompletableFuture.failedFuture(e);
        }
    }


    private PageResponse<?> convertToPageResponse(Page<Sentence> sentencePage, Pageable pageable) {
        List<SentenceDto> response = sentencePage.stream().map(this.sentenceMapper::toDto).collect(toList());
        return PageResponse.builder().items(response).totalItems(sentencePage.getTotalElements()).totalPage(sentencePage.getTotalPages()).hasNext(sentencePage.hasNext()).pageNo(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
    }
}
