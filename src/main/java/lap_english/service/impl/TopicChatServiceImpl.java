package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.LockStatusManager;
import lap_english.dto.TopicChatDto;
import lap_english.entity.TopicChat;
import lap_english.entity.User;
import lap_english.exception.DuplicateResource;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.TopicChatMapper;
import lap_english.repository.TopicChatRepo;
import lap_english.repository.UserRepo;
import lap_english.service.IAzureService;
import lap_english.service.ITopicChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TopicChatServiceImpl implements ITopicChatService {
    private final TopicChatRepo topicChatRepo;
    private final TopicChatMapper topicChatMapper;
    private final UserRepo userRepo;
    private final IAzureService azureService;

    @Override
    public void delete(Long id) {
        TopicChat topicChat = findTopicChatOrThrow(id);
        azureService.deleteBlob(topicChat.getImgUrl());
        topicChatRepo.delete(topicChat);
    }

    private TopicChat findTopicChatOrThrow(Long id) {
        return topicChatRepo.findById(id).orElseThrow(() -> {
            log.warn("Topic not found with id: {}", id);
            return new ResourceNotFoundException("Topic not found");
        });
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public TopicChatDto save(TopicChatDto dto, MultipartFile file) {
        User user = getCurrentUser();
        if (topicChatRepo.existsByUserId(user.getId())) {
            log.warn("User has already created a topic");
            throw new DuplicateResource("User has already created a topic");
        }
        TopicChat entity = topicChatMapper.toEntity(dto);

        entity.setUser(user);
        if (file == null || file.isEmpty() || file.getSize() == 0 || file.getOriginalFilename() == null) {
            return convertTopicChatToDto(topicChatRepo.save(entity));
        }
        entity.setImgUrl(azureService.upload(file));
        return convertTopicChatToDto(topicChatRepo.save(entity));
    }

    @Override
    public TopicChatDto update(TopicChatDto dto, MultipartFile file) {
        TopicChat entity = findTopicChatOrThrow(dto.getId());
        if (file != null) {
            azureService.deleteBlob(entity.getImgUrl());
            entity.setImgUrl(azureService.upload(file));
        }
        topicChatMapper.updateEntityFromDto(dto, entity);
        return convertTopicChatToDto(topicChatRepo.save(entity));
    }

    private TopicChatDto convertTopicChatToDto(TopicChat topicChat) {
        TopicChatDto topicChatDto = topicChatMapper.toDto(topicChat);
        LockStatusManager lockStatusManager = new LockStatusManager();
        lockStatusManager.setLocked(!topicChatRepo.existsByUserId(topicChat.getUser().getId()));
        lockStatusManager.setDiamound(topicChat.getDiamond());
        lockStatusManager.setGold(topicChat.getGold());
        topicChatDto.setStatus(lockStatusManager);
        return topicChatDto;
    }
}
