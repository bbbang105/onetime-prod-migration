package side.onetime.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberException extends RuntimeException {
    private final MemberErrorResult memberErrorResult;

    @Override
    public String getMessage() {
        return memberErrorResult.getMessage();
    }
}