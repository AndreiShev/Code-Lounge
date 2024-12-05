package ru.skillbox.controller.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.aspect.LogType;
import ru.skillbox.aspect.Loggable;
import ru.skillbox.dto.notification.NotificationCountDto;
import ru.skillbox.dto.notification.UserNotificationDto;
import ru.skillbox.service.NotificationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/page")
    @ResponseStatus(HttpStatus.OK)
    @Loggable(type = LogType.CONTROLLER)
    public Page<UserNotificationDto> getUserNotification(Pageable pageable) {
        return notificationService.getUserNotification(pageable);
    }

    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    @Loggable(type = LogType.CONTROLLER)
    public NotificationCountDto getUserNotificationCount() {
        return notificationService.getUserNotificationCount();
    }
}
