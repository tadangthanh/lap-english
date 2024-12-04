package lap_english.repository;

import lap_english.entity.CustomQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomQuizRepo extends JpaRepository<CustomQuiz, Long> {
}
