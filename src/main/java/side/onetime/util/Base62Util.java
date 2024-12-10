package side.onetime.util;

import org.springframework.stereotype.Component;
import java.math.BigInteger;

@Component
public class Base62Util {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String SHORTEN_DOMAIN = "https://1-ti.me/";
    private static final String ORIGINAL_DOMAIN = "https://www.onetime-with-members.com/";

    /**
     * Base62로 인코딩하는 메서드.
     *
     * 주어진 숫자를 Base62 문자열로 변환합니다.
     *
     * @param number 변환할 숫자 (BigInteger)
     * @return Base62로 인코딩된 문자열
     */
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

    /**
     * Base62로 디코딩하는 메서드.
     *
     * 주어진 Base62 문자열을 숫자(BigInteger)로 변환합니다.
     *
     * @param encodedString 디코딩할 Base62 문자열
     * @return 디코딩된 숫자 (BigInteger)
     */
    public static BigInteger decodeFromBase62(String encodedString) {
        BigInteger result = BigInteger.ZERO;
        BigInteger base = BigInteger.valueOf(62);
        for (int i = 0; i < encodedString.length(); i++) {
            result = result.multiply(base).add(BigInteger.valueOf(BASE62_CHARS.indexOf(encodedString.charAt(i))));
        }
        return result;
    }

    /**
     * 원본 URL을 단축 URL로 변환하는 메서드.
     *
     * 주어진 원본 URL에서 고유 식별자를 추출한 후, Base62로 인코딩하여 단축 URL을 생성합니다.
     *
     * @param originalUrl 단축할 원본 URL
     * @return 단축된 URL
     */
    public static String convertToShortenUrl(String originalUrl) {
        // 마지막 부분 고유 식별자 추출
        String[] parts = originalUrl.split("/");
        String uniquePart = parts[parts.length - 1]; // 고유 식별자 추출
        String cleanedPart = uniquePart.replace("-", ""); // '-' 제거

        BigInteger number = new BigInteger(cleanedPart, 16); // 16진수에서 숫자로 변환
        String encodedPart = encodeToBase62(number); // Base62로 인코딩

        return SHORTEN_DOMAIN + encodedPart;
    }

    /**
     * 단축 URL을 원본 URL로 복원하는 메서드.
     *
     * 주어진 단축 URL에서 고유 인코딩된 부분을 추출한 후, 디코딩하여 원본 URL을 복원합니다.
     *
     * @param shortenUrl 복원할 단축 URL
     * @return 복원된 원본 URL
     */
    public static String convertToOriginalUrl(String shortenUrl) {
        // 마지막 고유 인코딩된 부분 추출
        String[] parts = shortenUrl.split("/");
        String encodedPart = parts[parts.length - 1];

        BigInteger decodedNumber = decodeFromBase62(encodedPart); // Base62 디코딩
        String originalPart = decodedNumber.toString(16); // 16진수로 변환

        // '-' 제거된 형식을 원래의 UUID 형태로 복구할 수 있게 조정
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

        return ORIGINAL_DOMAIN + "events/" + restoredPart;
    }
}
