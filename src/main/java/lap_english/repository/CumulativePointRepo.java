package lap_english.repository;

import lap_english.entity.CumulativePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CumulativePointRepo extends JpaRepository<CumulativePoint, Long> {
    Optional<CumulativePoint> findByUserId(Long userId);
}
