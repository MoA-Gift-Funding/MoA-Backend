package moa.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import moa.global.s3.AwsS3Property;
import moa.global.s3.PresignedUrlClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.S3Presigner.Builder;

@Disabled
@Testcontainers
class PresignedUrlClientTest {

    @Container
    public LocalStackContainer localStackContainer = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack"))
            .withServices(S3);

    @Test
    void 이미지_확장자를_받아_이미지_이름을_UUID로_생성_후_프리사인드_URL을_생성하여_반환한다() {
        // given
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(
                localStackContainer.getAccessKey(),
                localStackContainer.getSecretKey()
        );
        Builder presignerBuilder = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(Region.of(localStackContainer.getRegion()));
        PresignedUrlClient service = new PresignedUrlClient(
                presignerBuilder,
                new AwsS3Property("mallang-bucket", "images/", 10)
        );

        // when
        var response = service.create("testImage.png");

        // then
        assertThat(response.presignedUrl()).contains(
                "https://mallang-bucket.s3.amazonaws.com/",
                "images/",
                ".png",
                "X-Amz-Expires=" + 60 * 10
        );
    }
}
