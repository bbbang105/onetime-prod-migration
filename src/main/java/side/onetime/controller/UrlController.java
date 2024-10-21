package side.onetime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import side.onetime.dto.url.request.ConvertToOriginalUrlRequest;
import side.onetime.dto.url.request.ConvertToShortenUrlRequest;
import side.onetime.dto.url.response.ConvertToOriginalUrlResponse;
import side.onetime.dto.url.response.ConvertToShortenUrlResponse;
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
    public ResponseEntity<ApiResponse<ConvertToShortenUrlResponse>> convertToShortenUrl(
            @RequestBody ConvertToShortenUrlRequest covertToShortenUrlRequest) {

        ConvertToShortenUrlResponse convertToShortenUrlResponse = urlService.convertToShortenUrl(covertToShortenUrlRequest);
        return ApiResponse.onSuccess(SuccessStatus._CONVERT_TO_SHORTEN_URL, convertToShortenUrlResponse);
    }

    // 단축 -> 원본 URL API
    @PostMapping("/action-original")
    public ResponseEntity<ApiResponse<ConvertToOriginalUrlResponse>> convertToOriginalUrl(
            @RequestBody ConvertToOriginalUrlRequest convertToOriginalUrlRequest) {

        ConvertToOriginalUrlResponse convertToOriginalUrlResponse = urlService.convertToOriginalUrl(convertToOriginalUrlRequest);
        return ApiResponse.onSuccess(SuccessStatus._CONVERT_TO_ORIGINAL_URL, convertToOriginalUrlResponse);
    }
}
