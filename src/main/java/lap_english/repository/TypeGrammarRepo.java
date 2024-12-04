package lap_english.repository;

import lap_english.entity.TypeGrammar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeGrammarRepo extends JpaRepository<TypeGrammar, Long> {
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END FROM TypeGrammar t WHERE lower(t.name) = lower(?1)")
    boolean existsByName(String name);
}
