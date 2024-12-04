package lap_english.repository;

import lap_english.entity.GrammaticalStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GrammaticalStructureRepo extends JpaRepository<GrammaticalStructure, Long>, JpaSpecificationExecutor<GrammaticalStructure> {
}
