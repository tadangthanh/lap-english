package lap_english.repository;

import jakarta.transaction.Transactional;
import lap_english.entity.UserDailyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDailyTaskRepo extends JpaRepository<UserDailyTask, Long> {
    List<UserDailyTask> findAllByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("delete from UserDailyTask udt where udt.dailyTask.id = ?1")
    void deleteAllByDailyTaskId(Long dailyTaskId);



}
