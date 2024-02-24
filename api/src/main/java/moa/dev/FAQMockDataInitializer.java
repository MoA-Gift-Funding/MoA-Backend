package moa.dev;

import static moa.cs.domain.QuestionCategory.CANCEL_REFUND;
import static moa.cs.domain.QuestionCategory.CREATE_FUNDING;
import static moa.cs.domain.QuestionCategory.DELIVERY;
import static moa.cs.domain.QuestionCategory.ETC;
import static moa.cs.domain.QuestionCategory.MEMBER;
import static moa.cs.domain.QuestionCategory.PARTICIPATE_FUNDING;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.cs.domain.FAQ;
import moa.cs.query.FAQQueryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class FAQMockDataInitializer implements CommandLineRunner {

    private final FAQQueryRepository faqQueryRepository;

    @Override
    public void run(String... args) throws Exception {
        faqQueryRepository.saveAll(List.of(
                new FAQ(CREATE_FUNDING, "펀딩 만드는 법이 궁금해요.", "네 ~ 알려드렸습니다~."),
                new FAQ(CREATE_FUNDING, "펀딩 만드는 법이 궁금해요. 2", "네 ~ 알려드렸습니다~."),
                new FAQ(PARTICIPATE_FUNDING, "펀딩 참여법이 궁금해요.", "네 ~ 알려드렸습니다~."),
                new FAQ(DELIVERY, "배달이 뭐죠?", "네 ~ 알려드렸습니다~."),
                new FAQ(CANCEL_REFUND, "취소 / 환불하는 법이 궁금해여", "네 ~ 알려드렸습니다~."),
                new FAQ(MEMBER, "아이디를 잃어버렸어요", "네 ~ 알려드렸습니다~."),
                new FAQ(ETC, "기타 치고싶어요.", "네 ~ 알려드렸습니다~.")
        ));
    }
}
