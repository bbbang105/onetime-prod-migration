package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import side.onetime.dto.url.request.ConvertToOriginalUrlRequest;
import side.onetime.dto.url.request.ConvertToShortenUrlRequest;
import side.onetime.dto.url.response.ConvertToOriginalUrlResponse;
import side.onetime.dto.url.response.ConvertToShortenUrlResponse;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.EventErrorStatus;
import side.onetime.repository.EventRepository;
import side.onetime.util.Base62Util;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final EventRepository eventRepository;

    /**
     * URL 단축 메서드.
     *
     * 주어진 원본 URL을 단축 URL로 변환합니다.
     * 변환된 URL은 Base62 인코딩 방식으로 생성됩니다.
     * URL에서 추출된 이벤트 ID가 유효한 이벤트인지 확인합니다.
     *
     * @param convertToShortenUrlRequest 단축 URL 요청 데이터
     * @return 단축 URL 응답 데이터
     */
    public ConvertToShortenUrlResponse convertToShortenUrl(ConvertToShortenUrlRequest convertToShortenUrlRequest) {
        String originalUrl = convertToShortenUrlRequest.originalUrl();

        UUID eventId = extractEventIdFromUrl(originalUrl);
        if (!eventRepository.existsByEventId(eventId)) {
            throw new CustomException(EventErrorStatus._NOT_FOUND_EVENT);
        }

        return ConvertToShortenUrlResponse.of(Base62Util.convertToShortenUrl(originalUrl));
    }

    /**
     * URL 복원 메서드.
     *
     * 주어진 단축 URL을 원본 URL로 복원합니다.
     * 복원된 URL에서 추출된 이벤트 ID가 유효한 이벤트인지 확인합니다.
     *
     * @param convertToOriginalUrlRequest 원본 URL 요청 데이터
     * @return 원본 URL 응답 데이터
     */
    public ConvertToOriginalUrlResponse convertToOriginalUrl(ConvertToOriginalUrlRequest convertToOriginalUrlRequest) {
        String shortenUrl = convertToOriginalUrlRequest.shortenUrl();
        String originalUrl = Base62Util.convertToOriginalUrl(shortenUrl);

        UUID eventId = extractEventIdFromUrl(originalUrl);
        if (!eventRepository.existsByEventId(eventId)) {
            throw new CustomException(EventErrorStatus._NOT_FOUND_EVENT);
        }

        return ConvertToOriginalUrlResponse.of(originalUrl);
    }

    /**
     * URL에서 Event ID 추출 메서드.
     *
     * 주어진 URL에서 마지막 부분에 포함된 이벤트 ID를 추출합니다.
     *
     * @param url 이벤트 ID를 추출할 URL
     * @return 추출된 이벤트 ID
     */
    private UUID extractEventIdFromUrl(String url) {
        String[] parts = url.split("/");
        return UUID.fromString(parts[parts.length - 1]);
    }
}
