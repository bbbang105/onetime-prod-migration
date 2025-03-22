package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.AdminUser;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    boolean existsAdminUsersByEmail(String email);
}
