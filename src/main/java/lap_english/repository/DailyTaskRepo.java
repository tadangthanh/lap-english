package lap_english.repository;

import lap_english.entity.DailyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyTaskRepo extends JpaRepository<DailyTask, Long> {
}
