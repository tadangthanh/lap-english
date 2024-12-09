package lap_english.repository;

import lap_english.entity.TopicChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicChatRepo extends JpaRepository<TopicChat, Long> {
    boolean existsByUserId(Long userId);
}
