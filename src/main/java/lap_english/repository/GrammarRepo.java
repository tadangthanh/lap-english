package lap_english.repository;

import lap_english.entity.Grammar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrammarRepo extends JpaRepository<Grammar, Long>, JpaSpecificationExecutor<Grammar> {
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN TRUE ELSE FALSE END FROM Grammar g WHERE lower(g.name) = lower(?1)")
    boolean existsByName(String name);
    @Query("SELECT g FROM Grammar g WHERE lower(g.name) = lower(?1)")
    List<Grammar> findByName(String name);
}
