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
import lap_english.service.IAzureService;
import lap_english.service.ISubTopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SubTopicServiceImpl implements ISubTopicService {
    private final SubTopicMapper subTopicMapper;
    private final SubTopicRepo subTopicRepo;
    private final MainTopicRepo mainTopicRepo;
    private final IAzureService azureService;

    @Override
    public SubTopicDto create(SubTopicDto subTopicDto) {
        validateSubTopic(subTopicDto);
        MainTopic mainTopic = getMainTopicById(subTopicDto.getMainTopicId());
        SubTopic subTopic = subTopicMapper.toEntity(subTopicDto);
        subTopic.setMainTopic(mainTopic);
        subTopic = subTopicRepo.save(subTopic);
        uploadImage(subTopicDto.getFile(), subTopic);
        return subTopicMapper.toDto(subTopic);
    }

    private void validateSubTopic(SubTopicDto subTopicDto) {
        if (this.subTopicRepo.existsByName(subTopicDto.getName())) {
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
        SubTopic subTopic = subTopicRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sub Topic not found"));
        String blobName = subTopic.getBlobName();
        subTopicRepo.delete(subTopic);
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

    public SubTopicDto update(SubTopicDto subTopicDto) {
        SubTopic subTopicExist = subTopicRepo.findById(subTopicDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sub Topic not found"));

        // Lưu đường dẫn file cần xóa
        String blobName = subTopicExist.getBlobName();

        subTopicMapper.updateEntityFromDto(subTopicDto, subTopicExist);
        subTopicExist = subTopicRepo.save(subTopicExist);


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

        uploadImage(subTopicDto.getFile(), subTopicExist);
        // Sau khi lưu thành công, trả về DTO
        return subTopicMapper.toDto(subTopicExist);
    }

    void deleteFile(String blobName) {
        if (blobName != null && !blobName.isEmpty()) {
            azureService.deleteBlob(blobName);
        }
    }

    @Override
    public PageResponse<?> getAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<SubTopic> subTopicPage = subTopicRepo.findAll(pageRequest);
        List<SubTopicDto> subTopicDtos = subTopicMapper.toListDto(subTopicPage.getContent());
        return PageResponse.builder()
                .items(subTopicDtos)
                .totalItems(subTopicPage.getTotalElements())
                .totalPage(subTopicPage.getTotalPages())
                .hasNext(subTopicPage.hasNext())
                .pageNo(page)
                .pageSize(size).build();
    }

    @Override
    public PageResponse<?> getByMainTopicId(Long mainTopicId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<SubTopic> subTopicPage = subTopicRepo.findByMainTopicId(mainTopicId, pageRequest);
        List<SubTopicDto> subTopicDtos = subTopicMapper.toListDto(subTopicPage.getContent());
        return PageResponse.builder()
                .items(subTopicDtos)
                .totalItems(subTopicPage.getTotalElements())
                .totalPage(subTopicPage.getTotalPages())
                .hasNext(subTopicPage.hasNext())
                .pageNo(page)
                .pageSize(size).build();
    }
}
