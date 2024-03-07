package moa.member.query;


import lombok.RequiredArgsConstructor;
import moa.member.domain.Member;
import moa.member.query.response.MemberResponse;
import moa.member.query.response.NotificationStatusResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberQueryRepository memberQueryRepository;

    public boolean existsDuplicatedEmail(String email) {
        return memberQueryRepository.existsByEmail(email);
    }

    public MemberResponse findMyProfile(Long memberId) {
        Member member = memberQueryRepository.getById(memberId);
        return MemberResponse.from(member);
    }

    public NotificationStatusResponse checkNotification(Long memberId) {
        Member member = memberQueryRepository.getById(memberId);
        return new NotificationStatusResponse(member.getPhone().getDeviceToken() != null);
    }
}
