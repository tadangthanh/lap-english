package lap_english.repository;

import lap_english.entity.UserLearnedSubTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLearnedSubTopicRepo extends JpaRepository<UserLearnedSubTopic, Long> {

    Optional<UserLearnedSubTopic> findByUserIdAndSubTopicId(Long userId, Long subTopicId);
}
