package lap_english.repository;

import lap_english.entity.UserSubTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubTopicRepo extends JpaRepository<UserSubTopic, Long> {
    boolean existsBySubTopicId(Long subTopicId);
    boolean existsByUserIdAndSubTopicId(Long userId, Long subTopicId);
}
