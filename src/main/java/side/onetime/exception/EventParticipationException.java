package side.onetime.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventParticipationException extends RuntimeException {
    private final EventParticipationErrorResult eventParticipationErrorResult;

    @Override
    public String getMessage() {
        return eventParticipationErrorResult.getMessage();
    }
}