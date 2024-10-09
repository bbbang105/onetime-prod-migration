package side.onetime.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import side.onetime.global.common.code.BaseErrorCode;
import side.onetime.global.common.dto.ErrorReasonDto;

@Getter
@RequiredArgsConstructor
public enum EventParticipationErrorResult implements BaseErrorCode {
    _NOT_FOUND_EVENT_PARTICIPATION(HttpStatus.NOT_FOUND, "404", "이벤트 참여 여부를 찾을 수 없습니다."),
    _IS_NOT_USERS_CREATED_EVENT_PARTICIPATION(HttpStatus.BAD_REQUEST, "400", "해당 이벤트의 생성자가 아닙니다."),
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