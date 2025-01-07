package side.onetime.repository.custom;

import side.onetime.domain.FixedEvent;

public interface FixedSelectionRepositoryCustom {
    void deleteFixedSelectionsByEvent(FixedEvent fixedEvent);
}
