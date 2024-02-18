package moa.product;

import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WincubeProductUpdateJobConfig {

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    public Job wincubeProductUpdateJob() {
        return new JobBuilder("wincubeProductUpdateJob", jobRepository)
                .start(wincubeProductUpdateStep(null))
                .build();
    }

    /**
     * 윈큐브 API를 호출하여 상품 목록을 받아온다. 상품 목록을 우리 서비스에서 사용하는 상품 엔티티(Product)로 변환한 뒤, 기존 상품들에 모두 덮어씌운다.
     */
    @Bean
    @JobScope
    public Step wincubeProductUpdateStep(
            @Value("#{jobParameters[now]}") LocalDateTime now
    ) {
        log.info("[취소 대기중인 결제 멱등키 재생성] 배치작업 수행 time: {}]", now);
        return new StepBuilder("wincubeProductUpdateStep", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

                        return null;
                    }
                }, transactionManager)
                .build();
    }
}
