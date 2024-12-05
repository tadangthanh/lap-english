package lap_english.repository;

import lap_english.entity.CustomQuiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomQuizRepo extends JpaRepository<CustomQuiz, Long> {
   Optional<CustomQuiz> findByExerciseGrammarId(Long exerciseGrammarId);

}
