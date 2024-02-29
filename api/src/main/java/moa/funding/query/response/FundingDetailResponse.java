package moa.funding.query.response;


import static moa.funding.domain.MessageVisibility.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import moa.friend.domain.Friend;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingParticipant;
import moa.funding.domain.FundingStatus;
import moa.member.domain.Member;
import moa.product.domain.Product;

public record FundingDetailResponse(
        @Schema(example = "1")
        Long id,

        @Schema(example = "1")
        Long memberId,

        @Schema(example = "주노", description = "펀딩을 등록한 회원의 닉네임")
        String nickName,

        @Schema(example = "https://펀딩-이미지-URL.com")
        String fundingImageUrl,

        @Schema(example = "나의 에어팟 펀딩")
        String title,

        @Schema(example = "다들 모여랏! 나에게 에어팟 맥스를 선물해 줄 기회! 기프티콘 줄거면 펀딩해주셈!")
        String description,

        @Schema(description = "생성일자", example = "2024-01-13 12:00:34")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdDate,

        @Schema(example = "2024-02-04")
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate endDate,

        @Schema(description = "최대 지불 가능금액. 남은 금액보다 큰 경우 남은금액으로 반환한다", example = "50000")
        Long maximumAmount,

        @Schema(description = "남은 금액", example = "140000")
        Long remainAmount,

        @Schema(description = "펀딩 달성 퍼센트", example = "56")
        int fundingRate,

        @Schema(description = "펀딩 상태", example = "PROCESSING")
        FundingStatus status,

        @Schema(description = "지금까지 펀딩된 금액", example = "50000")
        Long fundedAmount,

        @Schema(example = "17")
        Integer participationCount,

        @Schema(description = "상품 이미지", example = "https://imageurl.example")
        String productImageUrl,

        @Schema(description = "참여자 정보")
        List<Participant> participants
) {
    public record Participant(
            @Schema(description = "참여한 회원 ID", example = "1L")
            Long memberId,

            @Schema(description = "내가 등록한 친구 닉네임, 친구가 아니라면 해당 사람이 등록한 이름", example = "주노")
            String nickName,

            @Schema(description = "참여자 프로필 사진 Url", example = "https://example.com")
            String profileImageUrl,

            @Schema(description = "메시지 고유 ID", example = "1")
            Long messageId,

            @Schema(description = "메시지 내용", example = "형님이 보태준다")
            String message,

            @Schema(description = "메시지 작성 시간", example = "2024-11-02 12:00:01")
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime createAt
    ) {
        private static Participant of(
                Funding funding,
                FundingParticipant participant,
                Member member,
                List<Friend> friends
        ) {
            if (participant.getFundingMessage().getVisible() == PRIVATE
                && !isFundingOwner(funding, member) && !isMessageOwner(participant, member)) {
                return new Participant(
                        null,
                        null,
                        null,
                        null,
                        null,
                        participant.getCreatedDate()
                );
            }
            return getParticipant(participant, friends);
        }

        private static Participant getParticipant(FundingParticipant participant, List<Friend> friends) {
            String nickName = getNickName(participant.getMember(), friends);
            return new Participant(
                    participant.getMember().getId(),
                    nickName,
                    participant.getMember().getProfileImageUrl(),
                    participant.getFundingMessage().getId(),
                    participant.getFundingMessage().getContent(),
                    participant.getCreatedDate()
            );
        }
    }

    private static String getNickName(Member member, List<Friend> friends) {
        return friends.stream()
                .filter(friend -> friend.getTarget().equals(member))
                .findAny()
                .map(Friend::getNickname)
                .orElseGet(member::getNickname);
    }

    public static FundingDetailResponse of(Funding funding, Member member, List<Friend> friends) {
        Product product = funding.getProduct();
        var participants = funding.getParticipatingParticipants();
        return new FundingDetailResponse(
                funding.getId(),
                funding.getMember().getId(),
                getNickName(funding.getMember(), friends),
                funding.getImageUrl(),
                funding.getTitle(),
                funding.getDescription(),
                funding.getCreatedDate(),
                funding.getEndDate(),
                funding.possibleMaxAmount().longValue(),
                funding.remainAmount().longValue(),
                funding.getFundingRate(),
                funding.getStatus(),
                funding.getFundedAmount().longValue(),
                participants.size(),
                product.getImageUrl(),
                getMessages(funding, member, friends)
        );
    }

    private static List<Participant> getMessages(Funding funding, Member member, List<Friend> friends) {
        return funding.getParticipatingParticipants()
                .stream()
                .map(participant -> Participant.of(funding, participant, member, friends))
                .toList();
    }

    private static boolean isFundingOwner(Funding funding, Member member) {
        return Objects.equals(funding.getMember().getId(), member.getId());
    }

    private static boolean isMessageOwner(FundingParticipant participant, Member member) {
        return Objects.equals(participant.getMember().getId(), member.getId());
    }
}
