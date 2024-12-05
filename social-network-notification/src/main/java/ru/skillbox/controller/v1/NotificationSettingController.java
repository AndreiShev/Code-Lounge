package ru.skillbox.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.aspect.LogType;
import ru.skillbox.aspect.Loggable;
import ru.skillbox.dto.setting.CreateUserNotificationSettingsDto;
import ru.skillbox.dto.setting.NotificationSettingsDto;
import ru.skillbox.dto.setting.UpdateNotificationSettingsStatusDto;
import ru.skillbox.dto.setting.UserNotificationSettingsDto;
import ru.skillbox.service.NotificationSettingService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationSettingController {
    private final NotificationSettingService notificationSettingService;

    @PostMapping("/settings")
    @ResponseStatus(HttpStatus.CREATED)
    @Loggable(type = LogType.CONTROLLER)
    public CreateUserNotificationSettingsDto createUserNotificationSettings(
            @Valid @RequestBody CreateUserNotificationSettingsDto setting) {

        return notificationSettingService.createUserNotificationSettings(setting);
    }

    @PutMapping("/settings")
    @Loggable(type = LogType.CONTROLLER)
    public UpdateNotificationSettingsStatusDto updateUserNotificationSettings(
            @Valid @RequestBody NotificationSettingsDto settings) {
        return notificationSettingService.updateUserNotificationSettings(settings);
    }

    @GetMapping("/settings")
    @ResponseStatus(HttpStatus.OK)
    @Loggable(type = LogType.CONTROLLER)
    public UserNotificationSettingsDto getUserNotificationSettings() {

        return notificationSettingService.getUserNotificationSettings();
    }
}
