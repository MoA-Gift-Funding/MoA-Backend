package moa.customerservicecenter.query;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.customerservicecenter.query.response.PersonalInquiryResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonalInquireQueryService {

    private final PersonalInquireQueryRepository personalInquireQueryRepository;

    public List<PersonalInquiryResponse> findByMemberId(Long memberId) {
        return personalInquireQueryRepository.findByMemberId(memberId)
                .stream()
                .map(PersonalInquiryResponse::from)
                .toList();
    }
}
