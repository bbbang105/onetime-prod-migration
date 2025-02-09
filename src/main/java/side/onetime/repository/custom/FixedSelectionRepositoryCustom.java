package side.onetime.repository.custom;

import side.onetime.domain.FixedSelection;
import side.onetime.domain.User;

import java.util.List;

public interface FixedSelectionRepositoryCustom {
    void deleteFixedSelectionsByUser(User user);
    List<FixedSelection> findAllByUser(User user);
}
