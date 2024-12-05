package lap_english.repository;

import lap_english.entity.ExerciseGrammar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseGrammarRepo extends JpaRepository<ExerciseGrammar, Long>, JpaSpecificationExecutor<ExerciseGrammar> {

    Page<ExerciseGrammar> findByGrammaticalStructureId(Long grammaticalStructureId, Pageable pageable);


}
