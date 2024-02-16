package moa.notification.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class Notifications {

    private final List<Notification> notifications = new ArrayList<>();

    public Notifications(List<Notification> notifications) {
        this.notifications.addAll(notifications);
    }

    public void readAll() {
        for (Notification notification : notifications) {
            notification.read();
        }
    }
}
