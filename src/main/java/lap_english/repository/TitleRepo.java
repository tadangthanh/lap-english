package lap_english.repository;

import lap_english.entity.Title;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TitleRepo extends JpaRepository<Title,Long> {
    @Query("select case when count(t) > 0 then true else false end from Title t where t.task.id = ?1")
    boolean existByTaskId(Long taskId);

    Optional<Title> findByTaskId(Long taskId);
}
