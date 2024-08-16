package side.onetime.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SelectionException extends RuntimeException {
    private final SelectionErrorResult selectionErrorResult;

    @Override
    public String getMessage() {
        return selectionErrorResult.getMessage();
    }
}