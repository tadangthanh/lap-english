package lap_english.service.impl;
import jakarta.transaction.Transactional;
import lap_english.entity.Role;
import lap_english.repository.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements CommandLineRunner{
    private final RoleRepo roleRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Role> roles = this.initRole();
        roles.forEach((role) -> {
            if (!this.roleRepository.existsRoleByName(role.getName())) {
                this.roleRepository.save(role);
            }
        });
    }
    private List<Role> initRole() {
        Role r1 = new Role();
        r1.setName("ROLE_ADMIN");
        Role r2 = new Role();
        r2.setName("ROLE_USER");
        return List.of(r1, r2);
    }
}
