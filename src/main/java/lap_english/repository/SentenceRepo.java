package lap_english.repository;

import lap_english.entity.Sentence;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SentenceRepo extends JpaRepository<Sentence, Long>, JpaSpecificationExecutor<Sentence> {
    @Query("SELECT s FROM Sentence s WHERE s.subTopic.id = :subTopicId")
    Page<Sentence> findBySubTopicId(Long subTopicId, org.springframework.data.domain.Pageable pageable);

}
