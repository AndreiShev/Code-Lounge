package ru.skillbox.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.aspect.LogType;
import ru.skillbox.aspect.Loggable;
import ru.skillbox.dto.notification.NotificationCountDto;
import ru.skillbox.dto.notification.NotificationDto;
import ru.skillbox.dto.notification.UserNotificationDto;
import ru.skillbox.exception.InvalidDataException;
import ru.skillbox.exception.KafkaNotificationMessageException;
import ru.skillbox.factory.NotificationFactory;
import ru.skillbox.factory.UserNotificationSettingFactory;
import ru.skillbox.factory.notification.*;
import ru.skillbox.model.notification.NotificationEntity;
import ru.skillbox.model.notification.NotificationToUserEntity;
import ru.skillbox.model.setting.NotificationType;
import ru.skillbox.model.setting.UserNotificationSettingEntity;
import ru.skillbox.repository.NotificationRepository;
import ru.skillbox.repository.NotificationSettingRepository;
import ru.skillbox.repository.NotificationToUserRepository;
import ru.skillbox.service.NotificationService;
import ru.skillbox.service.integration.client.account.AccountServiceClient;
import ru.skillbox.service.integration.client.auth.AuthServiceClient;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImp implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationToUserRepository notificationToUserRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final AuthServiceClient authServiceClient;
    private final AccountServiceClient accountServiceClient;

    @Override
    @Loggable(type = LogType.SERVICE)
    @Transactional
    public void addNotification(String kafkaNotificationMessage, Long timestamp) {
        try {
            NotificationMessageType<? extends Notification> notification =
                    NotificationFactory.getNotificationMessageInstance(kafkaNotificationMessage);

            NotificationEntity notificationEntity =
                    notificationRepository.save(NotificationFactory.getNotificationEntity(notification, timestamp));

            List<UUID> toUserIds = getUserIdsForNotification(notification);

            notificationToUserRepository.saveAll(
                    NotificationFactory.getNotificationToUserEntities(notificationEntity, toUserIds));

        } catch (Exception e) {
            throw new InvalidDataException("Invalid message format");
        }
    }

    @Override
    @Loggable(type = LogType.SERVICE)
    @Transactional
    public Page<UserNotificationDto> getUserNotification(Pageable page) {
        String userAccountId = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<UserNotificationSettingEntity> userSetting = notificationSettingRepository
                .findByUserId(UUID.fromString(userAccountId));

        if (userSetting.isEmpty()) {
            notificationSettingRepository.save(
                    UserNotificationSettingFactory.getDefaultUserNotificationSettingEntity(userAccountId)
            );
        }

        Page<NotificationToUserEntity> notSendNotificationIds =
                notificationToUserRepository.findByToUserIddWithPageable(UUID.fromString(userAccountId), page);

        final List<NotificationType> availableForSendingNotificationType = getAvailableForSendingNotificationType(userAccountId);

        List<NotificationEntity> notificationsForUser = notSendNotificationIds
                .stream()
                .map(NotificationToUserEntity::getNotificationId)
                .filter(notification -> availableForSendingNotificationType.contains(
                        notification.getNotificationType())
                )
                .toList();

        log.info("Нашли notificationsForUser - всего {}", notificationsForUser.size());

        List<NotificationDto> dataForResponse = new ArrayList<>();
        for (NotificationEntity notification : notificationsForUser) {
            NotificationDto notificationDto = NotificationFactory.getNotificationDto(notification);
            dataForResponse.add(notificationDto);
        }

        log.info("Дошли до отправки ответа на вызов метода getUserNotification");

        return new PageImpl<>(
                List.of(NotificationFactory.getUserNotificationDto(dataForResponse)),
                page,
                notSendNotificationIds.getTotalElements());
    }

    @Override
    @Loggable(type = LogType.SERVICE)
    public NotificationCountDto getUserNotificationCount() {
        String userAccountId = SecurityContextHolder.getContext().getAuthentication().getName();

        Integer count = notificationToUserRepository.getCountNotSentUserNotification(UUID.fromString(userAccountId));

        return NotificationFactory.getNotificationCountDto(count);
    }

    private List<NotificationType> getAvailableForSendingNotificationType(String toUserId) {

        Map<NotificationType, Boolean> userNotificationSetting = UserNotificationSettingFactory
                .userNotificationSettingEntityToMap(
                        notificationSettingRepository
                                .findByUserId(UUID.fromString(toUserId))
                                .orElseThrow()
                );

        List<NotificationType> availableForSendingNotificationType = new ArrayList<>();
        for (NotificationType notificationType : NotificationType.values()) {
            boolean isActive = userNotificationSetting.get(notificationType);
            if (isActive) {
                availableForSendingNotificationType.add(notificationType);
            }
        }
        return availableForSendingNotificationType;
    }

    private List<UUID> getUserIdsForNotification(NotificationMessageType<? extends Notification> notification) {

        switch (notification.getNotificationInstance().getNotificationType()) {
            case FRIEND_REQUEST: {
                NotificationFriendRequest notificationFriendRequest =
                        (NotificationFriendRequest) notification.getNotificationInstance();

                return List.of((UUID.fromString(notificationFriendRequest.getToUserId())));
            }

            case POST_COMMENT: {
                NotificationPostComment notificationPostComment =
                        (NotificationPostComment) notification.getNotificationInstance();

                return List.of((UUID.fromString(notificationPostComment.getToUserId())));
            }

            case COMMENT_COMMENT: {
                NotificationCommentComment notificationCommentComment =
                        (NotificationCommentComment) notification.getNotificationInstance();

                return List.of((UUID.fromString(notificationCommentComment.getToUserId())));
            }

            case POST: {
                return List.of((UUID.fromString("fc26588d-cde2-4f64-b806-d5db04f81990")));
            }

            case MESSAGE: {
                NotificationMessage notificationMessage =
                        (NotificationMessage) notification.getNotificationInstance();

                return List.of((UUID.fromString(notificationMessage.getToUserId())));
            }
            default: {
                throw new KafkaNotificationMessageException("kafkaNotificationMessage created in invalid format");
            }
        }
    }

}
