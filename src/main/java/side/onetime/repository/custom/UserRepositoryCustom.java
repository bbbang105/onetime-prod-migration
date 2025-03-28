package side.onetime.repository.custom;

import org.springframework.data.domain.Pageable;
import side.onetime.domain.User;

import java.util.List;

public interface UserRepositoryCustom {

    void withdraw(User user);

    List<User> findAllWithSort(Pageable pageable, String keyword, String sorting);
}
