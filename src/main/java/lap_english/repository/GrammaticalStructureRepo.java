package lap_english.repository;

import jakarta.transaction.Transactional;
import lap_english.entity.GrammaticalStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrammaticalStructureRepo extends JpaRepository<GrammaticalStructure, Long>, JpaSpecificationExecutor<GrammaticalStructure> {
    @Modifying
    @Transactional
    @Query("delete from GrammaticalStructure gs where gs.grammar.id = ?1")
    void deleteAllByGrammarId(Long grammarId);

    List<GrammaticalStructure> findByGrammarId(Long grammarId);
}
