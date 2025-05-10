package side.onetime.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import side.onetime.auth.dto.CustomUserDetails;

public class UserAuthorizationUtil {

    private UserAuthorizationUtil() {
        throw new AssertionError();
    }

    /**
     * 현재 로그인한 사용자의 ID를 반환하는 메서드.
     *
     * SecurityContextHolder에서 Authentication을 가져와
     * CustomUserDetails로 캐스팅한 후, 사용자 ID를 추출합니다.
     *
     * @return 로그인된 사용자의 ID
     */
    public static Long getLoginUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}
