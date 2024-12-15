package lap_english.repository;

import lap_english.entity.UserDailyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDailyTaskRepo extends JpaRepository<UserDailyTask, Long> {
    List<UserDailyTask> findAllByUserId(Long userId);
}
