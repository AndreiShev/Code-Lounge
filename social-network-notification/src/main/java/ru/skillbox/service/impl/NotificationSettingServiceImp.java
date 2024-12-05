package ru.skillbox.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.aspect.LogType;
import ru.skillbox.aspect.Loggable;
import ru.skillbox.dto.setting.CreateUserNotificationSettingsDto;
import ru.skillbox.dto.setting.NotificationSettingsDto;
import ru.skillbox.dto.setting.UpdateNotificationSettingsStatusDto;
import ru.skillbox.dto.setting.UserNotificationSettingsDto;
import ru.skillbox.exception.InvalidDataException;
import ru.skillbox.factory.UserNotificationSettingFactory;
import ru.skillbox.model.setting.UserNotificationSettingEntity;
import ru.skillbox.repository.NotificationSettingRepository;
import ru.skillbox.service.NotificationSettingService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSettingServiceImp implements NotificationSettingService {
    private final NotificationSettingRepository notificationSettingRepository;
    private final ObjectMapper objectMapper;
    @Override
    @Loggable(type = LogType.SERVICE)
    @Transactional
    public void createUserNotificationSettings(String newAccount) {
        try {
            String accountId = objectMapper.readTree(newAccount).get("id").asText();

            notificationSettingRepository.save(
                    UserNotificationSettingFactory.getDefaultUserNotificationSettingEntity(accountId)
            );
        } catch (Exception e) {
            throw new InvalidDataException("Invalid message format");
        }
    }

    @Override
    @Loggable(type = LogType.SERVICE)
    @Transactional
    public CreateUserNotificationSettingsDto createUserNotificationSettings(CreateUserNotificationSettingsDto setting) {

        UserNotificationSettingEntity savedUserSetting = notificationSettingRepository
                .save(UserNotificationSettingFactory.getUserNotificationSettingEntity(setting));

        return UserNotificationSettingFactory.getCreateUserNotificationSettingsDto(savedUserSetting);
    }

    @Override
    @Loggable(type = LogType.SERVICE)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UpdateNotificationSettingsStatusDto updateUserNotificationSettings(NotificationSettingsDto updateSetting) {
        String userAccountId = SecurityContextHolder.getContext().getAuthentication().getName();

        UserNotificationSettingEntity userSetting = notificationSettingRepository
                .findByUserId(UUID.fromString(userAccountId))
                .orElseThrow();

        UserNotificationSettingEntity savedUserSetting = notificationSettingRepository
                .save(UserNotificationSettingFactory.updateUserNotificationSettingEntity(updateSetting, userSetting));

        log.info("Сохранили обновление настроект - смотрим по комент-комент: {}",
                savedUserSetting.getCommentComment()
        );

        return UserNotificationSettingFactory.getUpdateNotificationSettingsStatusDto(savedUserSetting);
    }

    @Override
    @Loggable(type = LogType.SERVICE)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserNotificationSettingsDto getUserNotificationSettings() {
        String userAccountId = SecurityContextHolder.getContext().getAuthentication().getName();

        UserNotificationSettingEntity userSetting = notificationSettingRepository
                .findByUserId(UUID.fromString(userAccountId))
                .orElseThrow();

        return UserNotificationSettingFactory.getUserNotificationSettingsDto(userSetting);
    }
}
