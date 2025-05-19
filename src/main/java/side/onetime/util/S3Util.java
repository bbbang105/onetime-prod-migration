package side.onetime.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Util {
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    /**
     * S3에 이미지 업로드 메서드.
     * 주어진 MultipartFile 이미지를 S3에 업로드하고 고유한 파일 이름을 반환합니다.
     *
     * @param directoryName 업로드할 디렉토리명
     * @param image          업로드할 이미지 파일
     * @return S3에 저장된 파일 이름
     * @throws IOException 파일 업로드 중 오류 발생 시
     */
    public String uploadImage(String directoryName, MultipartFile image) throws IOException {
        // 고유한 파일 이름 생성
        String fileName = directoryName + "/" + UUID.randomUUID() + "_" + image.getOriginalFilename();

        // S3에 파일 업로드 요청 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(image.getContentType())
                .build();

        // S3에 파일 업로드
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));

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
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);
    }

    /**
     * S3에서 파일 삭제 메서드.
     * 주어진 파일 이름에 해당하는 S3 파일을 삭제합니다.
     *
     * @param fileName S3에 저장된 파일 이름
     */
    public void deleteFile(String fileName) {
        // 파일 삭제
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
}
