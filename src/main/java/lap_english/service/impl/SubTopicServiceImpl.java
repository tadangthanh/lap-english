package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.SubTopicDto;
import lap_english.dto.response.BlobResponse;
import lap_english.dto.response.PageResponse;
import lap_english.entity.MainTopic;
import lap_english.entity.SubTopic;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.SubTopicMapper;
import lap_english.repository.MainTopicRepo;
import lap_english.repository.SubTopicRepo;
import lap_english.service.IAzureService;
import lap_english.service.ISubTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
        MainTopic mainTopic = getMainTopicById(subTopicDto.getMainTopicId());
        SubTopic subTopic = subTopicMapper.toEntity(subTopicDto);
        subTopic.setMainTopic(mainTopic);
        subTopic = subTopicRepo.save(subTopic);
        uploadFile(subTopicDto.getFile(), subTopic);


        return subTopicMapper.toDto(subTopic);
    }

    private void uploadFile(MultipartFile file, SubTopic subTopic) {
        BlobResponse blobResponse = azureService.upload(file);
        subTopic.setBlobName(blobResponse.getBlobName());
        subTopic.setImageUrl(blobResponse.getUrl());
    }


    private MainTopic getMainTopicById(Long id) {
        return mainTopicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Main Topic not found"));
    }

    @Override
    public void delete(Long id) {
        subTopicRepo.deleteById(id);
    }

    @Override
    public SubTopicDto update(SubTopicDto subTopicDto) {
        SubTopic subTopicExist = subTopicRepo.findById(subTopicDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Sub Topic not found"));
        subTopicMapper.updateEntityFromDto(subTopicDto, subTopicExist);
        subTopicExist = subTopicRepo.save(subTopicExist);
        return subTopicMapper.toDto(subTopicExist);
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
