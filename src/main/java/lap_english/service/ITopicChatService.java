package lap_english.service;

import lap_english.dto.TopicChatDto;
import lap_english.entity.TopicChat;
import org.springframework.web.multipart.MultipartFile;

public interface ITopicChatService {
    void delete(Long id);

    TopicChatDto save(TopicChatDto dto, MultipartFile file);

    TopicChatDto update(TopicChatDto dto,MultipartFile file);
}
