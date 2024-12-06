package side.onetime.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class FileUtil {

    public static MultipartFile convertToMultipartFile(byte[] fileContent, String fileName) throws IOException {
        return new MockMultipartFile(
                fileName,
                fileName,
                "image/png",
                new ByteArrayInputStream(fileContent)
        );
    }
}
