package side.onetime.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScheduleException extends RuntimeException {
    private final ScheduleErrorResult scheduleErrorResult;

    @Override
    public String getMessage() {
        return scheduleErrorResult.getMessage();
    }
}