package side.onetime.exception.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import side.onetime.global.common.code.BaseErrorCode;
import side.onetime.global.common.dto.ErrorReasonDto;

@Getter
@RequiredArgsConstructor
public enum AdminUserErrorStatus implements BaseErrorCode {
    _IS_DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "ADMIN-USER-001", "이미 존재하는 이메일입니다."),
    _NOT_FOUND_ADMIN_USER(HttpStatus.NOT_FOUND, "ADMIN-USER-002", "관리자 계정을 찾을 수 없습니다."),
    _IS_NOT_APPROVED_ADMIN_USER(HttpStatus.UNAUTHORIZED, "ADMIN-USER-003", "승인되지 않은 관리자 계정입니다."),
    _IS_NOT_EQUAL_PASSWORD(HttpStatus.BAD_REQUEST, "ADMIN-USER-004", "등록된 비밀번호와 다릅니다."),
    _ONLY_CAN_MASTER_ADMIN_USER(HttpStatus.UNAUTHORIZED, "ADMIN-USER-005", "마스터 관리자만 사용 가능한 기능입니다."),
    _INVALID_SORT_KEYWORD(HttpStatus.BAD_REQUEST, "ADMIN-USER-006", "지원하지 않는 정렬 기준입니다. (title, category, start_time, end_time, created_date, participant_count 중 하나여야 합니다.)"),
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
