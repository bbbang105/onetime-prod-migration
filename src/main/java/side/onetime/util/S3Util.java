package side.onetime.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Util {
    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * S3에 이미지 업로드 메서드.
     * 주어진 MultipartFile 이미지를 S3에 업로드하고 고유한 파일 이름을 반환합니다.
     *
     * @param image 업로드할 이미지 파일
     * @return S3에 저장된 파일 이름
     * @throws IOException 파일 업로드 중 오류 발생 시
     */
    public String uploadImage(MultipartFile image) throws IOException {
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename(); // 고유한 파일 이름 생성

        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());

        // S3에 파일 업로드 요청 생성
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, image.getInputStream(), metadata);

        // S3에 파일 업로드
        amazonS3.putObject(putObjectRequest);

        return fileName;
    }

    /**
     * S3에 저장된 파일의 퍼블릭 URL 반환 메서드.
     * 주어진 파일 이름에 해당하는 S3 파일의 퍼블릭 URL을 반환합니다.
     *
     * @param fileName S3에 저장된 파일 이름
     * @return 파일의 퍼블릭 URL
     */
    public String getPublicUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, amazonS3.getRegionName(), fileName);
    }
}
