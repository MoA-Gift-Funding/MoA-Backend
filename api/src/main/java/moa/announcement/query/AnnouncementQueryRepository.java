package moa.announcement.query;

import java.util.List;
import moa.announcement.domain.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AnnouncementQueryRepository extends JpaRepository<Announcement, Long> {

    @Query("""
            SELECT a FROM Announcement a
            ORDER BY a.createdDate DESC
            """)
    List<Announcement> findAllByCreatedDateDesc();
}
