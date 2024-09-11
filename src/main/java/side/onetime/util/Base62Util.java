package side.onetime.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
public class Base62Util {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    // Base62로 인코딩하는 메서드
    public static String encodeToBase62(BigInteger number) {
        if (number.equals(BigInteger.ZERO)) {
            return Character.toString(BASE62_CHARS.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        BigInteger base = BigInteger.valueOf(62);
        while (number.compareTo(BigInteger.ZERO) > 0) {
            int remainder = number.mod(base).intValue();
            sb.append(BASE62_CHARS.charAt(remainder));
            number = number.divide(base);
        }
        return sb.reverse().toString();
    }

    // Base62로 디코딩하는 메서드
    public static BigInteger decodeFromBase62(String encodedString) {
        BigInteger result = BigInteger.ZERO;
        BigInteger base = BigInteger.valueOf(62);
        for (int i = 0; i < encodedString.length(); i++) {
            result = result.multiply(base).add(BigInteger.valueOf(BASE62_CHARS.indexOf(encodedString.charAt(i))));
        }
        return result;
    }

    // 원본 -> 단축 URL 변환
    public static String convertToShortenUrl(String originalUrl) {
        // 마지막 부분 고유 식별자 추출
        String[] parts = originalUrl.split("/");
        String uniquePart = parts[parts.length - 1]; // 고유 식별자 추출
        String cleanedPart = uniquePart.replace("-", ""); // '-' 제거

        BigInteger number = new BigInteger(cleanedPart, 16); // 16진수에서 숫자로 변환
        String encodedPart = encodeToBase62(number); // Base62로 인코딩

        // "https://www.onetime-with-members.com/" 뒤에 인코딩된 고유값 추가
        return "onetime-with-members.com/" + encodedPart;
    }

    // 단축 -> 원본 URL 변환
    public static String convertToOriginalUrl(String shortenUrl) {
        // 마지막 고유 인코딩된 부분 추출
        String[] parts = shortenUrl.split("/");
        String encodedPart = parts[parts.length - 1];

        BigInteger decodedNumber = decodeFromBase62(encodedPart); // Base62 디코딩
        String originalPart = decodedNumber.toString(16); // 16진수로 변환

        // 원본 URL의 고유 부분을 복원할 때, 원래의 형식에 맞춰야 함
        // '-' 제거된 형식을 원래의 UUID 형태로 복구할 수 있게 조정 (예: 8-4-4-4-12 자리로 맞춤)
        StringBuilder restoredPart = new StringBuilder(originalPart);

        // 길이가 32자리가 아닐 경우 앞에 0을 붙여 맞춤
        while (restoredPart.length() < 32) {
            restoredPart.insert(0, "0");
        }

        // UUID 형식으로 복구
        restoredPart.insert(8, "-");
        restoredPart.insert(13, "-");
        restoredPart.insert(18, "-");
        restoredPart.insert(23, "-");

        // 복원된 고유 식별자를 원본 URL에 붙여서 반환
        return "https://www.onetime-with-members.com/events/" + restoredPart;
    }
}