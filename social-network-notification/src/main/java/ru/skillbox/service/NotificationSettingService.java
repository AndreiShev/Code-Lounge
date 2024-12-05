package ru.skillbox.service;

import ru.skillbox.dto.setting.CreateUserNotificationSettingsDto;
import ru.skillbox.dto.setting.NotificationSettingsDto;
import ru.skillbox.dto.setting.UpdateNotificationSettingsStatusDto;
import ru.skillbox.dto.setting.UserNotificationSettingsDto;

public interface NotificationSettingService {
    void createUserNotificationSettings(String newAccount);
    CreateUserNotificationSettingsDto createUserNotificationSettings(CreateUserNotificationSettingsDto setting);
    UpdateNotificationSettingsStatusDto updateUserNotificationSettings(NotificationSettingsDto settings);
    UserNotificationSettingsDto getUserNotificationSettings();

}
