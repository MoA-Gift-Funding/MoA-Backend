package moa.sms;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsHistoryRepository extends JpaRepository<SmsHistory, Long> {
}
