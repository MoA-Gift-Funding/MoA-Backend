package moa.friend.query.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import moa.friend.domain.Friend;
import moa.member.domain.Member;

public record FriendResponse(
        @Schema(description = "친구 PK")
        Long id,

        @Schema(description = "친구의 회원 id")
        Long memberId,

        @Schema(example = "https://image.com/imagepath")
        String profileImageUrl,

        @Schema(description = "내가 설정한 친구 별명", example = "내 친구 말랑")
        String customNickname,

        @Schema(description = "친구가 설정한 실제 별명", example = "신동훈")
        String realNickName,

        @Schema(description = "친구 전화번호", example = "010-1111-1111")
        String phoneNumber,

        @Schema(example = "0104")
        String birthday,

        @Schema(example = "2000")
        String birthyear,

        @Schema(description = "차단 여부")
        boolean isBlocked
) {
    public static List<FriendResponse> from(List<Friend> friends) {
        return friends.stream()
                .map(FriendResponse::from)
                .toList();
    }

    private static FriendResponse from(Friend friend) {
        Member target = friend.getTarget();
        return new FriendResponse(
                friend.getId(),
                target.getId(),
                target.getProfileImageUrl(),
                friend.getNickname(),
                target.getNickname(),
                target.getPhoneNumber(),
                target.getBirthday(),
                target.getBirthyear(),
                friend.isBlocked()
        );
    }
}
