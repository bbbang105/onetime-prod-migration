package side.onetime.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class QrUtil {

    @Value("${qr.event-base-url}")
    private String qrEventBaseUrl;

    public MultipartFile getQrCodeFile(UUID eventId) throws Exception {
        // QR 코드 생성
        byte[] qrCodeBytes = generateQRCode(qrEventBaseUrl + eventId);

        // 파일 이름 설정
        String fileName = String.format("qr");

        // ByteArray를 MultipartFile로 변환
        return FileUtil.convertToMultipartFile(qrCodeBytes, fileName);
    }

    public byte[] generateQRCode(String eventUrl) throws Exception {

        // JSON 형식의 데이터 생성
        String qrContent = String.format(eventUrl);

        // QR 코드 생성 객체
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // 인코딩 힌트 설정
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        // QR 코드 생성
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 100, 100, hints);

        // ByteArrayOutputStream을 사용해 이미지를 바이트 배열로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }
}
