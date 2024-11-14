package lap_english.repository;

import lap_english.entity.SubTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTopicRepo extends JpaRepository<SubTopic, Long> {
    @Query("SELECT s FROM SubTopic s WHERE s.mainTopic.id = ?1")
    Page<SubTopic> findByMainTopicId(Long mainTopicId, Pageable pageable);
}
