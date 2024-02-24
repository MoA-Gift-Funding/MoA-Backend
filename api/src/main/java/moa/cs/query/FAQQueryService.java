package moa.cs.query;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.cs.query.response.FAQResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FAQQueryService {

    private final FAQQueryRepository faqQueryRepository;

    public List<FAQResponse> findAll() {
        return faqQueryRepository.findAll()
                .stream()
                .map(FAQResponse::from)
                .toList();
    }
}
