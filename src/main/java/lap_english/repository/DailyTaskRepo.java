package lap_english.repository;

import lap_english.entity.DailyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DailyTaskRepo extends JpaRepository<DailyTask, Long> {
    @Query("select case when count(d) > 0 then true else false end from DailyTask d where d.task.id = ?1")
    boolean existByTaskId(Long taskId);
    @Query("select case when count(d) > 0 then true else false end from DailyTask d where d.reward.id = ?1")
    boolean existByRewardId(Long rewardId);

    Optional<DailyTask> findByTaskId(Long taskId);
}
