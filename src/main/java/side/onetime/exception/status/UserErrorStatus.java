package side.onetime.exception.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import side.onetime.global.common.code.BaseErrorCode;
import side.onetime.global.common.dto.ErrorReasonDto;

@Getter
@RequiredArgsConstructor
public enum UserErrorStatus implements BaseErrorCode {
    _NOT_FOUND_USER(HttpStatus.NOT_FOUND, "USER-001", "유저를 찾을 수 없습니다."),
    _NICKNAME_TOO_LONG(HttpStatus.BAD_REQUEST, "USER-002", "닉네임 길이 제한을 초과했습니다."),
    _NOT_FOUND_USER_BY_USERNAME(HttpStatus.NOT_FOUND, "USER-003", "username으로 user를 찾을 수 없습니다."),
    _NOT_FOUND_USER_BY_USERID(HttpStatus.NOT_FOUND, "USER-004", "userId로 user를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .isSuccess(false)
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }
}
