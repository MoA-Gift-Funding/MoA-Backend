//package moa.notification.application;
//
//import lombok.RequiredArgsConstructor;
//import moa.global.fcm.FcmClient;
//import moa.member.domain.Member;
//import moa.member.domain.MemberRepository;
//import moa.notification.domain.Notification;
//import moa.notification.domain.NotificationEvent;
//import moa.notification.domain.NotificationRepository;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class NotificationEventHandler {
//
//    private final MemberRepository memberRepository;
//    private final NotificationRepository notificationRepository;
//    private final FcmClient client;
//
//    TODO 루마와 얘기 후 푸쉬알림 구현
//    @EventListener(NotificationEvent.class)
//    public void push(NotificationEvent event) {
//        Member member = memberRepository.getById(event.memberId());
//        Notification notification = new Notification(event.url(), event.message(), member);
//        notificationRepository.save(notification);
//        String deviceToken = member.getPhone().getDeviceToken();
//        client.sendMessage(deviceToken, notification.getTitle(), notification.getMessage());
//    }
//}
