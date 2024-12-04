package lap_english.repository;

import lap_english.entity.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizAnswerRepo extends JpaRepository<QuizAnswer, Long> {
}
