package lap_english.repository;

import lap_english.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RewardRepo extends JpaRepository<Reward, Long> {
}
