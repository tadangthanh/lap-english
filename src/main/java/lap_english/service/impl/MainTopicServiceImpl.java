package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.MainTopicDto;
import lap_english.dto.response.PageResponse;
import lap_english.entity.MainTopic;
import lap_english.exception.DuplicateResource;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.MainTopicMapper;
import lap_english.repository.MainTopicRepo;
import lap_english.repository.specification.EntitySpecificationsBuilder;
import lap_english.service.IMainTopicService;
import lap_english.service.ISubTopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MainTopicServiceImpl implements IMainTopicService {
    private final MainTopicMapper mainTopicMapper;
    private final MainTopicRepo mainTopicRepo;
    private final ISubTopicService subTopicService;

    @Override
    public MainTopicDto create(MainTopicDto mainTopicDto) {
        checkExist(mainTopicDto.getName());
        MainTopic mainTopic = mainTopicMapper.toEntity(mainTopicDto);
        mainTopic = mainTopicRepo.save(mainTopic);
        return mainTopicMapper.toDto(mainTopic);
    }

    private void checkExist(String name) {
        if (mainTopicRepo.existByName(name)) {
            log.error("Main Topic is exist");
            throw new DuplicateResource("Main Topic is exist");
        }
    }

    @Override
    public void delete(Long id) {
        MainTopic mainTopic = mainTopicRepo.findById(id).orElseThrow(() -> {
            log.error("Main Topic not found");
            return new ResourceNotFoundException("Main Topic not found");
        });
        subTopicService.deleteByMainTopicId(id);
        mainTopicRepo.deleteById(id);
    }

    @Override
    public MainTopicDto update(MainTopicDto mainTopicDto) {
        checkExist(mainTopicDto.getName());
        MainTopic mainTopicExist = mainTopicRepo.findById(mainTopicDto.getId()).orElseThrow(() -> {
            log.error("Main Topic not found");
            return new ResourceNotFoundException("Main Topic not found");
        });
        mainTopicExist.setName(mainTopicDto.getName());
        return mainTopicMapper.toDto(mainTopicExist);
    }

    @Override
    public PageResponse<?> getPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<MainTopic> mainTopicPage = mainTopicRepo.findAll(pageRequest);
        List<MainTopicDto> mainTopicDtoList = mainTopicMapper.toListDto(mainTopicPage.getContent());
        return PageResponse.builder()
                .items(mainTopicDtoList)
                .totalItems(mainTopicPage.getTotalElements())
                .totalPage(mainTopicPage.getTotalPages())
                .hasNext(mainTopicPage.hasNext())
                .pageNo(page)
                .pageSize(size).build();
    }

    @Override
    public List<MainTopicDto> getAll() {
        List<MainTopic> mainTopics = mainTopicRepo.findAll();
        return mainTopicMapper.toListDto(mainTopics);
    }

    @Override
    public PageResponse<?> advanceSearchBySpecification(Pageable pageable, String[] mainTopic) {
        if (mainTopic != null && mainTopic.length > 0) {
            EntitySpecificationsBuilder<MainTopic> builder = new EntitySpecificationsBuilder<>();
            Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)"); //?page=0&size=10&sort=id,desc&subtopic=name~d
            for (String s : mainTopic) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }

            Page<MainTopic> mainTopicPage = mainTopicRepo.findAll(builder.build(), pageable);

            return convertToPageResponse(mainTopicPage, pageable);
        }
        return convertToPageResponse(mainTopicRepo.findAll(pageable), pageable);
    }

    private PageResponse<?> convertToPageResponse(Page<MainTopic> mainTopicPage, Pageable pageable) {
        List<MainTopicDto> response = mainTopicPage.stream().map(this.mainTopicMapper::toDto).collect(toList());
        return PageResponse.builder().items(response).totalItems(mainTopicPage.getTotalElements()).totalPage(mainTopicPage.getTotalPages()).hasNext(mainTopicPage.hasNext()).pageNo(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
    }
}
