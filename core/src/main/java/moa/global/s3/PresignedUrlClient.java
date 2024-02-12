package moa.global.s3;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
@RequiredArgsConstructor
public class PresignedUrlClient {

    private final S3Presigner.Builder presignerBuilder;
    private final AwsS3Property s3Property;

    public String create(String fileName) {
        try (S3Presigner presigner = presignerBuilder.build()) {
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(s3Property.presignedUrlExpiresMinutes()))
                    .putObjectRequest(builder -> builder
                            .bucket(s3Property.bucket())
                            .key(s3Property.imagePath() + fileName)
                            .build()
                    ).build();
            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toExternalForm();
        }
    }
}
