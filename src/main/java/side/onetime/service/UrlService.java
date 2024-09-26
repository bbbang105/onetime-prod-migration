package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import side.onetime.dto.UrlDto;
import side.onetime.exception.EventErrorResult;
import side.onetime.exception.EventException;
import side.onetime.repository.EventRepository;
import side.onetime.util.Base62Util;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final EventRepository eventRepository;

    // URL 단축 메서드
    public UrlDto.ConvertToShortenUrlResponse convertToShortenUrl(UrlDto.ConvertToShortenUrlRequest convertToShortenUrlRequest) {
        String originalUrl = convertToShortenUrlRequest.getOriginalUrl();

        UUID eventId = extractEventIdFromUrl(originalUrl);
        if (!eventRepository.existsByEventId(eventId)) {
            throw new EventException(EventErrorResult._NOT_FOUND_EVENT);
        }

        return UrlDto.ConvertToShortenUrlResponse.of(Base62Util.convertToShortenUrl(originalUrl));
    }

    // URL 복원 메서드
    public UrlDto.ConvertToOriginalUrlResponse convertToOriginalUrl(UrlDto.ConvertToOriginalUrlRequest convertToOriginalUrlRequest) {
        String shortenUrl = convertToOriginalUrlRequest.getShortenUrl();
        String originalUrl = Base62Util.convertToOriginalUrl(shortenUrl);

        UUID eventId = extractEventIdFromUrl(originalUrl);
        if (!eventRepository.existsByEventId(eventId)) {
            throw new EventException(EventErrorResult._NOT_FOUND_EVENT);
        }

        return UrlDto.ConvertToOriginalUrlResponse.of(originalUrl);
    }

    // URL에서 Event ID 추출
    private UUID extractEventIdFromUrl(String url) {
        String[] parts = url.split("/");
        return UUID.fromString(parts[parts.length - 1]);
    }
}