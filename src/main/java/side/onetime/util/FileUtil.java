package side.onetime.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class FileUtil {

    /**
     * 바이트 배열을 MultipartFile로 변환하는 메서드.
     *
     * 주어진 파일 내용(byte 배열)과 파일 이름을 이용하여 MockMultipartFile 객체를 생성합니다.
     * 이 메서드는 테스트 또는 파일 변환 작업에 유용하게 사용할 수 있습니다.
     *
     * @param fileContent 변환할 파일의 바이트 배열
     * @param fileName 파일 이름
     * @return 변환된 MultipartFile 객체
     * @throws IOException 파일 변환 중 발생할 수 있는 입출력 예외
     */
    public static MultipartFile convertToMultipartFile(byte[] fileContent, String fileName) throws IOException {
        return new MockMultipartFile(
                fileName,
                fileName,
                "image/png",
                new ByteArrayInputStream(fileContent)
        );
    }
}
