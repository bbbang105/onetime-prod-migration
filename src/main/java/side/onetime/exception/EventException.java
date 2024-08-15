package side.onetime.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventException extends RuntimeException {
    private final EventErrorResult eventErrorResult;

    @Override
    public String getMessage() {
        return eventErrorResult.getMessage();
    }
}