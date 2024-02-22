package moa.announcement.query;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.announcement.query.response.AnnouncementResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementQueryService {

    private final AnnouncementQueryRepository announcementQueryRepository;

    public List<AnnouncementResponse> findAll() {
        return AnnouncementResponse.from(announcementQueryRepository.findAllByCreatedDateDesc());
    }
}
