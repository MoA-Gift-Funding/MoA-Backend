package moa.dev;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.announcement.domain.Announcement;
import moa.announcement.domain.AnnouncementRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class AnnouncementMockDataInitializer implements CommandLineRunner {

    private final AnnouncementRepository announcementRepository;

    @Override
    public void run(String... args) throws Exception {
        announcementRepository.saveAll(List.of(
                new Announcement("모아가 출시되었습니다.", "짝짝짝"),
                new Announcement("모아 공지 2", "루마는 왜 신이 나셨나요?"),
                new Announcement("모아 공지 3", "'신이 나니까'")
        ));
    }
}
