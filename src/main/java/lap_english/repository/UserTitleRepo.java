package lap_english.repository;

import jakarta.transaction.Transactional;
import lap_english.entity.UserTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTitleRepo extends JpaRepository<UserTitle,Long> {
    List<UserTitle> findAllByUserId(Long userId);
    @Transactional
    @Modifying
    @Query("delete from UserTitle ut where ut.title.id = ?1")
    void deleteAllByTitleId(Long titleId);
    @Query("select ut from UserTitle ut where ut.title.id = ?1 and ut.user.id = ?2 and FUNCTION('DATE', ut.createdAt) = FUNCTION('DATE', ?3)")
    Optional<UserTitle>findByTitleIdAndUserId(Long titleId, Long userId, Date createdAt);
}
