package ru.skillbox.factory.notification;

public class NotificationMessageType<T extends Notification>{
    private T notification;

    public NotificationMessageType(T notification) {
        this.notification = notification;
    }

    public T getNotificationInstance() {
        return notification;
    }


}
