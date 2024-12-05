package ru.skillbox.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.skillbox.dto.notification.NotificationCountDto;
import ru.skillbox.dto.notification.UserNotificationDto;

public interface NotificationService {
    void addNotification(String kafkaNotificationMessage, Long timestamp);
    Page<UserNotificationDto> getUserNotification(Pageable notifications);

    NotificationCountDto getUserNotificationCount();
}
