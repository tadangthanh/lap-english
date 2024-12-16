package lap_english.repository;

import lap_english.entity.UserLearnedSubTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLearnedSubTopicRepo extends JpaRepository<UserLearnedSubTopic, Long> {

    Optional<UserLearnedSubTopic> findByUserIdAndSubTopicId(Long userId, Long subTopicId);
    boolean existsBySubTopicId(Long subTopicId);
    @Query("select count(u) from UserLearnedSubTopic u where u.user.id = ?1 and u.subTopic.id = ?2")
    boolean existByUserIdAndSubtopicId(Long userId, Long subTopicId);
}
