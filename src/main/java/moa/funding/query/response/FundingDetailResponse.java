package moa.funding.query.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import moa.friend.domain.Friend;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingParticipant;
import moa.member.domain.Member;
import moa.product.domain.Product;

public record FundingDetailResponse(
        @Schema(example = "1")
        Long id,

        @Schema(example = "1")
        Long memberId,

        @Schema(example = "나의 에어팟 펀딩")
        String title,

        @Schema(example = "다들 모여랏! 나에게 에어팟 맥스를 선물해 줄 기회! 기프티콘 줄거면 펀딩해주셈!")
        String description,

        @Schema(example = "2024-02-04")
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate endDate,

        @Schema(description = "최대 지불 가능금액. 남은 금액보다 큰 경우 남은금액으로 반환한다", example = "50000")
        Long maximumAmount,

        @Schema(description = "남은 금액", example = "140000")
        Long leftAmount,

        @Schema(example = "56")
        int fundingRate,

        @Schema(description = "펀딩 상태 / 준비중, 진행중, 완료, 취소", example = "진행중")
        String fundingStatus,

        @Schema(description = "지금까지 펀딩된 금액", example = "50000")
        Long fundedAmount,

        @Schema(example = "17")
        Integer participationCount,

        @Schema(description = "상품 이미지", example = "https://imageurl.example")
        String productImageUrl,

        @Schema(description = "메시지")
        List<Message> message
) {
    public record Message(
            @Schema(description = "메시지 작성자 닉네임", example = "주노")
            String nickName,

            @Schema(description = "메시지 작성자 프로필 사진 Url", example = "https://example.com")
            String profileImageUrl,

            @Schema(description = "메시지 내용", example = "형님이 보태준다")
            String message,

            @Schema(description = "메시지 작성 시간", example = "2024-11-02 12:00:01")
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createAt
    ) {
        private static Message of(FundingParticipant participant, List<Friend> friends) {
            String nickName = friends.stream()
                    .filter(friend -> Objects.equals(friend.getTarget().getId(), participant.getMember().getId()))
                    .findAny()
                    .map(Friend::getNickname)
                    .orElseGet(() -> participant.getMember().getNickname());
            return new Message(
                    nickName,
                    participant.getMember().getProfileImageUrl(),
                    participant.getMessage(),
                    participant.getCreatedDate()
            );
        }
    }

    public static FundingDetailResponse of(Funding funding, Member member, List<Friend> friends) {
        Product product = funding.getProduct();
        return new FundingDetailResponse(
                funding.getId(),
                member.getId(),
                funding.getTitle(),
                funding.getDescription(),
                funding.getEndDate(),
                funding.possibleMaxAmount().longValue(),
                funding.remainAmount().longValue(),
                funding.getFundingRate(),
                funding.getStatus().getDescription(),
                funding.getFundedAmount().longValue(),
                funding.getParticipants().size(),
                product.getImageUrl(),
                getMessages(funding, friends)
        );
    }

    private static List<Message> getMessages(Funding funding, List<Friend> friends) {
        return funding.getParticipants()
                .stream()
                .map(participant -> Message.of(participant, friends))
                .toList();
    }
}
