package moa.client.wincube.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WincubeIssueAuthTokenRequest(
        @JsonProperty("codeId") String codeId
) {
}
