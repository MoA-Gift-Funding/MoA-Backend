package moa.notification.domain;

import java.util.List;
import java.util.Optional;
import moa.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    default Notifications findNotifications(Member member) {
        return new Notifications(findByMember(member));
    }

    @Query("""
            SELECT n
            FROM Notification n
            WHERE n.member = :member
            ORDER BY n.createdDate DESC
            """)
    List<Notification> findByMember(Member member);

    default boolean existsByUnread(Member member) {
        return findUnreadByMember(member)
                .isPresent();
    }

    @Query("""
            SELECT n
            FROM Notification n
            WHERE n.member = :member
            AND n.isRead = FALSE
            ORDER BY n.createdDate DESC
            LIMIT 1
            """)
    Optional<Notification> findUnreadByMember(Member member);
}
