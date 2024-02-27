package moa.client.oauth.kakao.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUnlinkRequest(
        @JsonProperty("target_id_type")
        String targetIdType,

        @JsonProperty("target_id")
        String memberOauthId
) {
}
