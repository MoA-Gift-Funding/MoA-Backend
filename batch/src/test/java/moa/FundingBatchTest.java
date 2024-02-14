package moa;

import static moa.funding.BatchFundingConfig.JOB_NAME;
import static org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

import java.time.LocalDate;
import java.time.LocalDateTime;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.funding.domain.FundingStatus;
import moa.funding.domain.FundingVisibility;
import moa.global.domain.Price;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.member.domain.OauthId;
import moa.member.domain.OauthId.OauthProvider;
import moa.product.domain.Product;
import moa.product.domain.ProductRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@SpringBatchTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class FundingBatchTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private FundingRepository fundingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void 기간이_지난_펀딩의_상태가_EXPIRED가_된다(@Autowired Job job) throws Exception {
        jobLauncherTestUtils.setJob(job);

        var now = LocalDateTime.now();
        Member member = memberRepository.save(new Member(
                new OauthId("1", OauthProvider.APPLE),
                null,
                "주노",
                "2000",
                "06",
                "testImageUrl",
                "010-1234-5678"
        ));
        Product product = productRepository.save(new Product("exampleProduct", Price.from("10000")));

        Funding 펀딩_만료_1 = 펀딩_생성(now.toLocalDate().minusDays(2), member, product, now);
        Funding 펀딩_만료_2 = 펀딩_생성(now.toLocalDate().minusDays(1), member, product, now);
        Funding 펀딩_정상_1 = 펀딩_생성(now.toLocalDate().plusDays(2), member, product, now);
        Funding 펀딩_정상_2 = 펀딩_생성(now.toLocalDate().plusDays(4), member, product, now);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(JOB_NAME, LocalDateTime.now().toString())
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        var expectExpired1 = fundingRepository.getById(펀딩_만료_1.getId()).getStatus();
        var expectExpired2 = fundingRepository.getById(펀딩_만료_2.getId()).getStatus();
        var expectProcess1 = fundingRepository.getById(펀딩_정상_1.getId()).getStatus();
        var expectProcess2 = fundingRepository.getById(펀딩_정상_2.getId()).getStatus();

        SoftAssertions.assertSoftly(
                softly -> {
                    softly.assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
                    softly.assertThat(expectExpired1).isEqualTo(FundingStatus.EXPIRED);
                    softly.assertThat(expectExpired2).isEqualTo(FundingStatus.EXPIRED);
                    softly.assertThat(expectProcess1).isEqualTo(FundingStatus.PROCESSING);
                    softly.assertThat(expectProcess2).isEqualTo(FundingStatus.PROCESSING);
                }
        );
    }

    private Funding 펀딩_생성(LocalDate expireDate, Member member, Product product, LocalDateTime now) {
        return fundingRepository.save(
                new Funding(
                        "testImageUrl",
                        "exampleFundingTitle",
                        "exampleFundingDescription",
                        expireDate,
                        FundingVisibility.PUBLIC,
                        Price.from("15000"),
                        member,
                        product,
                        null,
                        ""
                ));
    }
}
