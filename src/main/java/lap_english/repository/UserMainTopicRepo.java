package lap_english.repository;

import lap_english.entity.UserMainTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMainTopicRepo extends JpaRepository<UserMainTopic, Long> {
    boolean existsByUserIdAndMainTopicId(Long userId, Long mainTopicId);
}
