package moa.client.wincube.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WincubeIssueAuthCodeRequest(
        @JsonProperty("custId") String custId,  // 업체 AES256 암호화
        @JsonProperty("pwd") String pwd,  // 업체 AES256 암호화
        @JsonProperty("autKey") String autKey,  // 업체 AES256 암호화
        @JsonProperty("aesKey") String aesKey,  // RSA 암호화
        @JsonProperty("aesIv") String aesIv  // RSA 암호화
) {
}
