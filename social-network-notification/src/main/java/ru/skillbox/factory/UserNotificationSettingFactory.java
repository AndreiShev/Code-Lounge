package ru.skillbox.factory;

import lombok.experimental.UtilityClass;
import ru.skillbox.dto.setting.*;
import ru.skillbox.model.setting.NotificationType;
import ru.skillbox.model.setting.UserNotificationSettingEntity;

import java.util.*;

@UtilityClass
public class UserNotificationSettingFactory {

    public static UserNotificationSettingEntity getDefaultUserNotificationSettingEntity(String accountId) {

        return UserNotificationSettingEntity.builder()
                .userId(UUID.fromString(accountId))
                .friendRequest(Boolean.FALSE)
                .friendBirthday(Boolean.FALSE)
                .postComment(Boolean.FALSE)
                .commentComment(Boolean.FALSE)
                .post(Boolean.FALSE)
                .message(Boolean.FALSE)
                .sendPhoneMessage(Boolean.FALSE)
                .sendEmailMessage(Boolean.FALSE)
                .build();
    }

    public static UserNotificationSettingEntity getUserNotificationSettingEntity(
            CreateUserNotificationSettingsDto settings) {

        return UserNotificationSettingEntity.builder()
                .userId(UUID.fromString(settings.getUserId()))
                .friendRequest(settings.getFriendRequest())
                .friendBirthday(settings.getFriendBirthday())
                .postComment(settings.getPostComment())
                .commentComment(settings.getCommentComment())
                .post(settings.getPost())
                .message(settings.getMessage())
                .sendPhoneMessage(settings.getSendPhoneMessage())
                .sendEmailMessage(settings.getSendEmailMessage())
                .build();
    }

    public static CreateUserNotificationSettingsDto getCreateUserNotificationSettingsDto(
            UserNotificationSettingEntity settings) {

        return CreateUserNotificationSettingsDto.builder()
                .id(settings.getId().toString())
                .userId(settings.getUserId().toString())
                .friendRequest(settings.getFriendRequest())
                .friendBirthday(settings.getFriendBirthday())
                .postComment(settings.getPostComment())
                .commentComment(settings.getCommentComment())
                .post(settings.getPost())
                .message(settings.getMessage())
                .sendPhoneMessage(settings.getSendPhoneMessage())
                .sendEmailMessage(settings.getSendEmailMessage())
                .build();
    }

    public static UserNotificationSettingEntity updateUserNotificationSettingEntity(
            NotificationSettingsDto updateSetting,
            UserNotificationSettingEntity currentUserSettingEntity) {

        return updateEnableNotificationTypesForEntity(
                updateSetting.getNotificationType(),
                updateSetting.getEnable(),
                currentUserSettingEntity);
    }

    public static UpdateNotificationSettingsStatusDto getUpdateNotificationSettingsStatusDto(
            UserNotificationSettingEntity setting) {

        return UpdateNotificationSettingsStatusDto.builder()
                .time(setting.getChangeDate().toString())
                .status(List.of(new NotificationSettingsStatus(Boolean.TRUE)))
                .build();
    }


    public static UserNotificationSettingsDto getUserNotificationSettingsDto(UserNotificationSettingEntity setting) {

        List<NotificationSettingsDto> settingsDto = new ArrayList<>();
        for (NotificationType notificationType : NotificationType.values()) {
            NotificationSettingsDto userSetting = NotificationSettingsDto.builder()
                    .enable(notificationTypesIsEnableForUser(notificationType, setting))
                    .notificationType(notificationType)
                    .build();

            settingsDto.add(userSetting);
        }

        return UserNotificationSettingsDto.builder()
                .time(setting.getChangeDate().toString())
                .data(settingsDto)
                .userId(setting.getUserId().toString())
                .build();
    }

    private static Boolean notificationTypesIsEnableForUser(
            NotificationType notificationType,
            UserNotificationSettingEntity setting) {
        switch (notificationType) {
            case FRIEND_REQUEST -> {
                return setting.getFriendRequest();
            }
            case FRIEND_BIRTHDAY -> {
                return setting.getFriendBirthday();
            }
            case POST_COMMENT -> {
                return setting.getPostComment();
            }
            case COMMENT_COMMENT -> {
                return setting.getCommentComment();
            }
            case POST -> {
                return setting.getPost();
            }
            case MESSAGE -> {
                return setting.getMessage();
            }
            case SEND_PHONE_MESSAGE -> {
                return setting.getSendPhoneMessage();
            }
            case SEND_EMAIL_MESSAGE -> {
                return setting.getSendEmailMessage();
            }
            default -> {
                return false;
            }
        }
    }

    private static UserNotificationSettingEntity updateEnableNotificationTypesForEntity(
            NotificationType notificationType,
            Boolean isEnable,
            UserNotificationSettingEntity setting) {
        switch (notificationType) {
            case FRIEND_REQUEST -> setting.setFriendRequest(isEnable);
            case FRIEND_BIRTHDAY -> setting.setFriendBirthday(isEnable);
            case POST_COMMENT -> setting.setPostComment(isEnable);
            case COMMENT_COMMENT -> setting.setCommentComment(isEnable);
            case POST -> setting.setPost(isEnable);
            case MESSAGE -> setting.setMessage(isEnable);
            case SEND_PHONE_MESSAGE -> setting.setSendPhoneMessage(isEnable);
            case SEND_EMAIL_MESSAGE -> setting.setSendEmailMessage(isEnable);
        }
        return setting;
    }

    public static Map<NotificationType, Boolean> userNotificationSettingEntityToMap(
            UserNotificationSettingEntity userSettingEntity) {

        Map<NotificationType, Boolean> setting= new HashMap<>();
        for (NotificationType notificationType : NotificationType.values()) {
            switch (notificationType) {
                case FRIEND_REQUEST -> setting.put(notificationType, userSettingEntity.getFriendRequest());
                case FRIEND_BIRTHDAY -> setting.put(notificationType, userSettingEntity.getFriendBirthday());
                case POST_COMMENT -> setting.put(notificationType, userSettingEntity.getPostComment());
                case COMMENT_COMMENT -> setting.put(notificationType, userSettingEntity.getCommentComment());
                case POST -> setting.put(notificationType, userSettingEntity.getPost());
                case MESSAGE -> setting.put(notificationType, userSettingEntity.getMessage());
                case SEND_PHONE_MESSAGE -> setting.put(notificationType, userSettingEntity.getSendPhoneMessage());
                case SEND_EMAIL_MESSAGE -> setting.put(notificationType, userSettingEntity.getSendEmailMessage());
            }
        }
        return setting;
    }

}
