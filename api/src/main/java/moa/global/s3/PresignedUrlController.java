package moa.global.s3;

import static moa.member.domain.MemberStatus.PRESIGNED_UP;
import static moa.member.domain.MemberStatus.SIGNED_UP;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import moa.auth.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/infra/aws/s3/presigned-url")
public class PresignedUrlController implements PresignedUrlApi {

    private final PresignedUrlClient presignedUrlClient;

    @PostMapping
    public ResponseEntity<CreatePresignedUrlResponse> createPresignedUrl(
            @Auth(permit = {PRESIGNED_UP, SIGNED_UP}) Long memberId,
            @Valid @RequestBody CreatePresignedUrlRequest request
    ) {
        String name = generateFileName(request.fileName());
        String url = presignedUrlClient.create(name);
        return ResponseEntity.ok(new CreatePresignedUrlResponse(url, name));
    }

    private static String generateFileName(String fileName) {
        String[] split = fileName.split("\\.");
        String fileExt = split[split.length - 1];
        return UUID.randomUUID() + "." + fileExt;
    }
}
