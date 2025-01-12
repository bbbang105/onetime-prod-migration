package side.onetime.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import side.onetime.auth.dto.CustomUserDetails;
import side.onetime.domain.User;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.UserErrorStatus;
import side.onetime.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 사용자 이름으로 사용자 정보를 로드합니다.
     *
     * 데이터베이스에서 주어진 사용자 이름(username)을 기반으로 사용자를 조회하고,
     * CustomUserDetails 객체로 래핑하여 반환합니다.
     *
     * @param username 사용자 이름
     * @return 사용자 상세 정보 (CustomUserDetails 객체)
     * @throws CustomException 사용자 이름에 해당하는 사용자가 없을 경우 예외를 발생시킵니다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new CustomException(UserErrorStatus._NOT_FOUND_USER_BY_USERNAME));
        return new CustomUserDetails(user);
    }

    /**
     * 사용자 ID로 사용자 정보를 로드합니다.
     *
     * 데이터베이스에서 주어진 사용자 ID를 기반으로 사용자를 조회하고,
     * CustomUserDetails 객체로 래핑하여 반환합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 상세 정보 (CustomUserDetails 객체)
     * @throws CustomException 사용자 ID에 해당하는 사용자가 없을 경우 예외를 발생시킵니다.
     */
    public UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorStatus._NOT_FOUND_USER_BY_USERID));
        return new CustomUserDetails(user);
    }
}
