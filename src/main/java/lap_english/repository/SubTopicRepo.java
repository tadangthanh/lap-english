package lap_english.repository;

import lap_english.entity.SubTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTopicRepo extends JpaRepository<SubTopic, Long>, JpaSpecificationExecutor<SubTopic> {
    @Query("SELECT s FROM SubTopic s WHERE s.mainTopic.id = ?1")
    Page<SubTopic> findByMainTopicId(Long mainTopicId, Pageable pageable);
    @Query("SELECT CASE WHEN COUNT(sp) > 0 THEN TRUE ELSE FALSE END FROM SubTopic sp WHERE lower(sp.name) = lower(?1)")
    boolean existsByName(String name);
    @Query("SELECT s FROM SubTopic s WHERE lower(s.name) LIKE lower(concat('%', ?1, '%'))")
    Page<SubTopic> findByNameContaining(String name, Pageable pageable);
}
