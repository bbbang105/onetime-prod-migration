package side.onetime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import side.onetime.dto.UrlDto;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.status.SuccessStatus;
import side.onetime.service.UrlService;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    // 원본 -> 단축 URL API
    @PostMapping("/action-shorten")
    public ResponseEntity<ApiResponse<UrlDto.ConvertToShortenUrlResponse>> convertToShortenUrl(
            @RequestBody UrlDto.ConvertToShortenUrlRequest covertToShortenUrlRequest) {

        UrlDto.ConvertToShortenUrlResponse convertToShortenUrlResponse = urlService.convertToShortenUrl(covertToShortenUrlRequest);
        return ApiResponse.onSuccess(SuccessStatus._CONVERT_TO_SHORTEN_URL, convertToShortenUrlResponse);
    }

    // 단축 -> 원본 URL API
    @PostMapping("/action-original")
    public ResponseEntity<ApiResponse<UrlDto.ConvertToOriginalUrlResponse>> convertToOriginalUrl(
            @RequestBody UrlDto.ConvertToOriginalUrlRequest convertToOriginalUrlRequest) {

        UrlDto.ConvertToOriginalUrlResponse convertToOriginalUrlResponse = urlService.convertToOriginalUrl(convertToOriginalUrlRequest);
        return ApiResponse.onSuccess(SuccessStatus._CONVERT_TO_ORIGINAL_URL, convertToOriginalUrlResponse);
    }
}
