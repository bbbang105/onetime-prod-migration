package side.onetime.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import side.onetime.global.common.code.BaseErrorCode;
import side.onetime.global.common.dto.ErrorReasonDto;

@Getter
@RequiredArgsConstructor
public enum MemberErrorResult implements BaseErrorCode {
    _NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "404", "멤버를 찾을 수 없습니다."),
    _IS_EXISTED_NAME(HttpStatus.CONFLICT, "409", "이미 존재하는 이름입니다."),
    _IS_ALREADY_REGISTERED(HttpStatus.CONFLICT, "409", "이미 등록된 멤버입니다."),
    _NOT_FOUND_MEMBERS(HttpStatus.NOT_FOUND, "404", "멤버 전체 목록을 가져오는 데 실패했습니다.")
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