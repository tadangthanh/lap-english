package lap_english.repository;

import lap_english.entity.Word;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepo extends JpaRepository<Word, Long>, JpaSpecificationExecutor<Word> {
    @Query("select w from Word w where w.subTopic.id = ?1")
    Page<Word> findBySubTopicId(Long subTopicId, Pageable pageable);
    @Query("select count(w) from Word w where w.subTopic.id = ?1")
    int countBySubTopicId(Long subTopicId);
    @Query("select w from Word w where w.subTopic.id = ?1")
    List<Word> findAllBySubTopicId(Long subTopicId);
}
