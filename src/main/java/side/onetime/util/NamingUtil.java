package side.onetime.util;

public class NamingUtil {

    /**
     * snake_case 문자열을 camelCase로 변환합니다.
     * 예: "created_date" -> "createdDate"
     *
     * @param snakeCase 변환할 snake_case 문자열
     * @return 변환된 camelCase 문자열
     */
    public static String toCamelCase(String snakeCase) {
        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;
        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    result.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }
}
