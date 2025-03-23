package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.AdminUser;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    boolean existsAdminUsersByEmail(String email);
    Optional<AdminUser> findAdminUserByEmail(String email);
}
