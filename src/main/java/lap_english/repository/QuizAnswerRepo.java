package lap_english.repository;

import lap_english.entity.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAnswerRepo extends JpaRepository<QuizAnswer, Long> {
    @Query("SELECT qa FROM QuizAnswer qa WHERE qa.customQuiz.id = ?1")
    List<QuizAnswer> findByCustomQuizId(Long id);
}
