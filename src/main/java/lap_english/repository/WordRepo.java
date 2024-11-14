package lap_english.repository;

import lap_english.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepo extends JpaRepository<Word, Long> {
    @Query("select w from Word w where w.subTopic.id = ?1")
    Page<Word> findBySubTopicId(Long subTopicId, Pageable pageable);
}
