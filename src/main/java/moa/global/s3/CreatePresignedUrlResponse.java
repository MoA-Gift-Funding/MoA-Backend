package moa.global.s3;

public record CreatePresignedUrlResponse(
        String presignedUrl,
        String fileName
) {
}
