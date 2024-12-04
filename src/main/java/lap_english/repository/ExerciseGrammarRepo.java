package lap_english.repository;

import lap_english.entity.ExerciseGrammar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseGrammarRepo extends JpaRepository<ExerciseGrammar, Long> {
}
