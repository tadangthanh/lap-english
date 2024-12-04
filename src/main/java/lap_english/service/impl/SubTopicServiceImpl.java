package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.SubTopicDto;
import lap_english.dto.response.PageResponse;
import lap_english.entity.MainTopic;
import lap_english.entity.SubTopic;
import lap_english.exception.DuplicateResource;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.SubTopicMapper;
import lap_english.repository.MainTopicRepo;
import lap_english.repository.SubTopicRepo;
import lap_english.repository.WordRepo;
import lap_english.repository.specification.EntitySpecificationsBuilder;
import lap_english.service.IAzureService;
import lap_english.service.ISubTopicService;
import lap_english.service.IWordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SubTopicServiceImpl implements ISubTopicService {
    private final SubTopicMapper subTopicMapper;
    private final SubTopicRepo subTopicRepo;
    private final MainTopicRepo mainTopicRepo;
    private final IAzureService azureService;
    private final WordRepo wordRepo;
    private final IWordService wordService;

    @Override
    public SubTopicDto create(SubTopicDto subTopicDto, MultipartFile file) {
        validateSubTopic(subTopicDto);
        MainTopic mainTopic = getMainTopicById(subTopicDto.getMainTopicId());
        SubTopic subTopic = subTopicMapper.toEntity(subTopicDto);
        subTopic.setMainTopic(mainTopic);
        subTopic = subTopicRepo.save(subTopic);
        uploadImage(file, subTopic);
        return subTopicMapper.toDto(subTopic);
    }

    private void validateSubTopic(SubTopicDto subTopicDto) {
        if (this.subTopicRepo.existsByNameAndMainTopicId(subTopicDto.getName(), subTopicDto.getMainTopicId())) {
            throw new DuplicateResource("Sub Topic already exists");
        }
    }

    private void uploadImage(MultipartFile file, SubTopic subTopic) {
        if (file == null || !Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            return;
        }
        subTopic.setBlobName(azureService.upload(file));
    }


    private MainTopic getMainTopicById(Long id) {
        return mainTopicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Main Topic not found"));
    }

    @Override
    public void delete(Long id) {
        SubTopic subTopic = subTopicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sub Topic not found"));
        String blobName = subTopic.getBlobName();
        subTopicRepo.delete(subTopic);
        wordService.deleteBySubTopicId(id);
        // Đăng ký hành động xóa file sau khi transaction commit thành công
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deleteFile(blobName);
            }

            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) {
                    // Thực hiện hành động khác nếu transaction không thành công
                    log.error("Transaction rolled back, no file deletion.");
                }
            }
        });
    }

    public SubTopicDto update(SubTopicDto subTopicDto, MultipartFile file) {
        SubTopic subTopicExist = subTopicRepo.findById(subTopicDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Sub Topic not found"));

        // Lưu đường dẫn file cần xóa
        String blobName = subTopicExist.getBlobName();

        subTopicMapper.updateEntityFromDto(subTopicDto, subTopicExist);
        subTopicExist = subTopicRepo.save(subTopicExist);


        // Đăng ký hành động xóa file sau khi transaction commit thành công
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                if (file != null) {
                    deleteFile(blobName);
                }
            }

            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) {
                    // Thực hiện hành động khác nếu transaction không thành công
                    log.error("Transaction rolled back, no file deletion.");
                }
            }
        });

        uploadImage(file, subTopicExist);
        // Sau khi lưu thành công, trả về DTO
        SubTopicDto result = subTopicMapper.toDto(subTopicExist);
        result.setWordCount(wordRepo.countBySubTopicId(subTopicExist.getId()));
        return result;
    }

    void deleteFile(String blobName) {
        if (blobName != null && !blobName.isEmpty()) {
            azureService.deleteBlob(blobName);
        }
    }

    @Override
    public PageResponse<?> getPage(int page, int size, String sort) {
        PageRequest pageRequest = PageRequest.of(page, size, sort.equals("asc") ? Sort.by("name").ascending() : Sort.by("name").descending());
        Page<SubTopic> subTopicPage = subTopicRepo.findAll(pageRequest);
        List<SubTopicDto> subTopicDtos = subTopicMapper.toListDto(subTopicPage.getContent());
        subTopicDtos.forEach(subTopicDto -> {
            subTopicDto.setWordCount(wordRepo.countBySubTopicId(subTopicDto.getId()));
        });
        return PageResponse.builder().items(subTopicDtos).totalItems(subTopicPage.getTotalElements()).totalPage(subTopicPage.getTotalPages()).hasNext(subTopicPage.hasNext()).pageNo(page).pageSize(size).build();
    }

    @Override
    public PageResponse<?> getByMainTopicId(Long mainTopicId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<SubTopic> subTopicPage = subTopicRepo.findByMainTopicId(mainTopicId, pageRequest);
        List<SubTopicDto> subTopicDtos = subTopicMapper.toListDto(subTopicPage.getContent());
        return PageResponse.builder().items(subTopicDtos).totalItems(subTopicPage.getTotalElements()).totalPage(subTopicPage.getTotalPages()).hasNext(subTopicPage.hasNext()).pageNo(page).pageSize(size).build();
    }

    @Override
    public PageResponse<?> findByName(String name, int page, int size, String sort) {
        PageRequest pageRequest = PageRequest.of(page, size, sort.equals("asc") ? Sort.by("name").ascending() : Sort.by("name").descending());
        Page<SubTopic> subTopicPage = subTopicRepo.findByNameContaining(name, pageRequest);
        List<SubTopicDto> subTopicDtos = subTopicMapper.toListDto(subTopicPage.getContent());
        subTopicDtos.forEach(subTopicDto -> {
            subTopicDto.setWordCount(wordRepo.countBySubTopicId(subTopicDto.getId()));
        });
        return PageResponse.builder().items(subTopicDtos).totalItems(subTopicPage.getTotalElements()).totalPage(subTopicPage.getTotalPages()).hasNext(subTopicPage.hasNext()).pageNo(page).pageSize(size).build();
    }

    @Override
    public PageResponse<?> advanceSearchBySpecification(Pageable pageable, String[] subTopic) {
        log.info("request get all of sub topic with specification");
        if (subTopic != null && subTopic.length > 0) {
            EntitySpecificationsBuilder<SubTopic> builder = new EntitySpecificationsBuilder<SubTopic>();
            Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)"); //?page=0&size=10&sort=id,desc&subtopic=name~d
            for (String s : subTopic) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }

            Page<SubTopic> subTopicPage = subTopicRepo.findAll(builder.build(), pageable);

            return convertToPageResponse(subTopicPage, pageable);
        }
        return convertToPageResponse(subTopicRepo.findAll(pageable), pageable);
    }

    @Override
    public void deleteByMainTopicId(Long mainTopicId) {
        List<SubTopic> subTopics = subTopicRepo.findAllByMainTopicId(mainTopicId);
        for (SubTopic subTopic : subTopics) {
            wordService.deleteBySubTopicId(subTopic.getId());
            subTopicRepo.delete(subTopic);
        }
    }

    @Override
    public SubTopicDto getById(Long id) {
        SubTopic subTopic = subTopicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sub Topic not found"));
        SubTopicDto subTopicDto = subTopicMapper.toDto(subTopic);
        subTopicDto.setWordCount(wordRepo.countBySubTopicId(subTopicDto.getId()));
        return subTopicDto;
    }

    private PageResponse<?> convertToPageResponse(Page<SubTopic> subTopicPage, Pageable pageable) {
        List<SubTopicDto> response = subTopicPage.stream().map(this.subTopicMapper::toDto).collect(toList());
        response.forEach(subTopicDto -> {
            subTopicDto.setWordCount(wordRepo.countBySubTopicId(subTopicDto.getId()));
        });
        return PageResponse.builder().items(response).totalItems(subTopicPage.getTotalElements()).totalPage(subTopicPage.getTotalPages()).hasNext(subTopicPage.hasNext()).pageNo(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
    }
}
