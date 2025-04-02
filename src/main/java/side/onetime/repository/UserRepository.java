package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.User;
import side.onetime.repository.custom.UserRepositoryCustom;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByName(String name);
    User findByProviderId(String providerId);
    void withdraw(User user);
}
