package lap_english.repository;

import lap_english.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role,Long> {
    boolean existsRoleByName(String name);
    Optional<Role> findRoleByName(String name);
}
