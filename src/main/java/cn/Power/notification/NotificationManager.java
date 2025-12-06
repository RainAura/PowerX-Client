package cn.Power.notification;

import java.util.ArrayList;

import cn.Power.Client;
import cn.Power.notification.Notification.Type;

public class NotificationManager {
    private ArrayList<Notification> notifications = new ArrayList<>();

    public void addNotification(String message, int stayTime, final Type type) {
        notifications.add(new Notification(message, stayTime, type));
    }
    public void addNotification(String message, final Type type) {
        notifications.add(new Notification(message, 500, type));
    }
    public ArrayList<Notification> getNotifications() {
        return notifications;
    }
}
