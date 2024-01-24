package moa.member.query;


import lombok.RequiredArgsConstructor;
import moa.member.domain.Member;
import moa.member.query.response.MemberResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberQueryRepository memberQueryRepository;

    public MemberResponse findMyProfile(Long memberId) {
        Member member = memberQueryRepository.getById(memberId);
        return MemberResponse.from(member);
    }
}
