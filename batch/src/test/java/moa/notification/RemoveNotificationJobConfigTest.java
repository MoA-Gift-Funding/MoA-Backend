package moa.notification;

import static moa.fixture.MemberFixture.member;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.batch.core.BatchStatus.COMPLETED;

import java.time.LocalDateTime;
import moa.BatchTest;
import moa.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@BatchTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class RemoveNotificationJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private Job removeNotificationJob;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(removeNotificationJob);
    }

    @Test
    void 알림_제거_15일이_지난_알림만_제거된다() throws Exception {
        jobLauncherTestUtils.setJob(removeNotificationJob);
        // given
        var member = memberRepository.save(member(null, "1", "010-1111-1111", SIGNED_UP));
        LocalDateTime now = LocalDateTime.of(2024, 1, 20, 0, 0, 0);

        jdbcTemplate.update("""
                INSERT INTO notification(url, title, message, member_id, created_date, is_read)
                VALUES ('testUrl', 'testTitle', 'testMessage', ?, ?, false)
                """, member.getId(), LocalDateTime.of(2024, 1, 4, 0, 0, 0)); // 삭제 대상
        jdbcTemplate.update("""
                INSERT INTO notification(url, title, message, member_id, created_date, is_read)
                VALUES ('testUrl', 'testTitle', 'testMessage', ?, ?, false)
                """, member.getId(), LocalDateTime.of(2024, 1, 5, 0, 0, 0));
        jdbcTemplate.update("""
                INSERT INTO notification(url, title, message, member_id, created_date, is_read)
                VALUES ('testUrl', 'testTitle', 'testMessage', ?, ?, false)
                """, member.getId(), LocalDateTime.of(2024, 1, 6, 0, 0, 0));
        // when
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("now", now)
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM notification", Integer.class);
        assertThat(count).isEqualTo(2);
    }
}
