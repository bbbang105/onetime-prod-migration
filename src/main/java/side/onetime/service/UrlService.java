package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import side.onetime.dto.UrlDto;
import side.onetime.util.Base62Util;

@Service
@RequiredArgsConstructor
public class UrlService {

    // URL 단축 메서드
    public UrlDto.ConvertToShortenUrlResponse convertToShortenUrl(UrlDto.ConvertToShortenUrlRequest convertToShortenUrlRequest) {
        String originalUrl = convertToShortenUrlRequest.getOriginalUrl();
        return UrlDto.ConvertToShortenUrlResponse.of(Base62Util.convertToShortenUrl(originalUrl));
    }

    // URL 단축 메서드
    public UrlDto.ConvertToOriginalUrlResponse convertToOriginalUrl(UrlDto.ConvertToOriginalUrlRequest convertToOriginalUrlRequest) {
        String shortenUrl = convertToOriginalUrlRequest.getShortenUrl();
        return UrlDto.ConvertToOriginalUrlResponse.of(Base62Util.convertToOriginalUrl(shortenUrl));
    }
}