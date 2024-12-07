package lap_english.repository;

import lap_english.entity.CumulativePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CumulativePointRepo extends JpaRepository<CumulativePoint, Long> {
}
