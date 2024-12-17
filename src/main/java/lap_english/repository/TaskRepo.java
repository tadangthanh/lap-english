package lap_english.repository;

import io.lettuce.core.dynamic.annotation.Param;
import lap_english.entity.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task,Long>, JpaSpecificationExecutor<Task> {
    @Query("select t from Task t order by FUNCTION('RAND') ")
    List<Task> findRandomTasks(Pageable pageable);
    @Query("SELECT t FROM Task t WHERE t.id NOT IN :excludedIds ORDER BY FUNCTION('RAND')")
    List<Task> findRandomTasks(@Param("excludedIds") List<Long> excludedIds, Pageable pageable);

}
