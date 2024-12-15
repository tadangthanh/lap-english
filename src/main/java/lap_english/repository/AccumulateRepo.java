package lap_english.repository;

import lap_english.entity.Accumulate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccumulateRepo extends JpaRepository<Accumulate,Long> {
}
