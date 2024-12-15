package lap_english.repository;

import lap_english.entity.UserTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTitleRepo extends JpaRepository<UserTitle,Long> {
    List<UserTitle> findAllByUserId(Long userId);
}
