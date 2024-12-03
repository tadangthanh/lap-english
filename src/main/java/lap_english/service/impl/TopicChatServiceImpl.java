package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.TopicChatDto;
import lap_english.entity.TopicChat;
import lap_english.entity.User;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.TopicChatMapper;
import lap_english.repository.TopicChatRepo;
import lap_english.repository.UserRepo;
import lap_english.service.IAzureService;
import lap_english.service.ITopicChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class TopicChatServiceImpl implements ITopicChatService {
    private final TopicChatRepo topicChatRepo;
    private final TopicChatMapper topicChatMapper;
    private final UserRepo userRepo;
    private final IAzureService azureService;

    @Override
    public void delete(Long id) {
        TopicChat topicChat = topicChatRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
        azureService.deleteBlob(topicChat.getBlobName());
        topicChatRepo.delete(topicChat);
    }

    @Override
    public TopicChatDto save(TopicChatDto dto, MultipartFile file) {
        TopicChat entity = topicChatMapper.toEntity(dto);
        User user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        entity.setUser(user);
        entity.setBlobName(azureService.upload(file));
        return topicChatMapper.toDto(topicChatRepo.save(entity));
    }

    @Override
    public TopicChatDto update(TopicChatDto dto, MultipartFile file) {
        TopicChat entity = topicChatRepo.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
        if (file != null) {
            azureService.deleteBlob(entity.getBlobName());
            entity.setBlobName(azureService.upload(file));
        }
        topicChatMapper.updateEntityFromDto(dto, entity);
        return topicChatMapper.toDto(topicChatRepo.save(entity));
    }
}
