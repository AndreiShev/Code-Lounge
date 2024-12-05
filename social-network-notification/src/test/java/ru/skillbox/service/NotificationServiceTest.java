package ru.skillbox.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import ru.skillbox.BaseTestClass;
import ru.skillbox.dto.setting.CreateUserNotificationSettingsDto;
import ru.skillbox.exception.InvalidDataException;
import ru.skillbox.factory.UserNotificationSettingFactory;
import ru.skillbox.factory.notification.*;
import ru.skillbox.model.setting.NotificationType;
import ru.skillbox.model.setting.UserNotificationSettingEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "bbcbd6aa-4145-4778-9855-bb053105b87c")
class NotificationServiceTest extends BaseTestClass {

    private CreateUserNotificationSettingsDto settings;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void before() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        settings = CreateUserNotificationSettingsDto.builder()
                .userId(username)
                .friendRequest(true)
                .friendBirthday(true)
                .postComment(true)
                .commentComment(true)
                .post(true)
                .message(true)
                .sendPhoneMessage(true)
                .sendEmailMessage(true)
                .build();
    }



    private static Stream<Notification> provideNotificationObjects() {
        return Stream.of(
                new NotificationCommentComment(
                        UUID.randomUUID().toString(),
                        NotificationType.COMMENT_COMMENT,
                        "Ответил на комент",
                        "bbcbd6aa-4145-4778-9855-bb053105b87c"),
                new NotificationFriendRequest(
                        UUID.randomUUID().toString(),
                        NotificationType.FRIEND_REQUEST,
                        "Запрос в друзья",
                        "bbcbd6aa-4145-4778-9855-bb053105b87c"),
                new NotificationMessage(
                        UUID.randomUUID().toString(),
                        NotificationType.MESSAGE,
                        "Отправил сообщение",
                        "bbcbd6aa-4145-4778-9855-bb053105b87c"),
                new NotificationPostComment(
                        UUID.randomUUID().toString(),
                        NotificationType.POST_COMMENT,
                        "Оставил комент на пост",
                        "bbcbd6aa-4145-4778-9855-bb053105b87c")
        );
    }

    @ParameterizedTest
    @DisplayName("add user notification should create new user notification")
    @MethodSource("provideNotificationObjects")
    void when_addNotification_then_addUserNotification(Notification notification) throws Exception {
        UserNotificationSettingEntity userNotificationSettingEntity =
                UserNotificationSettingFactory.getUserNotificationSettingEntity(settings);
        notificationSettingRepository.save(userNotificationSettingEntity);

        String message = objectMapper.writeValueAsString(notification);
        notificationServiceImp.addNotification(message, LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());

        mockMvc
                .perform(get("/api/v1/notifications/page")
                        .param("page", "O")
                        .param("sort", "sentTime,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].data[0].notificationType").value(notification.getNotificationType().toString()))
                .andExpect(jsonPath("$.content[0].data[0].content").value(notification.getContent()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    @DisplayName("add user notification should throw exception")
    void given_invalidNotificType_when_addNotification_then_addUserNotification() {
        UserNotificationSettingEntity userNotificationSettingEntity =
                UserNotificationSettingFactory.getUserNotificationSettingEntity(settings);
        notificationSettingRepository.save(userNotificationSettingEntity);

        String message = "{\n" +
                "\t\"authorId\": \"cfe5f13b-eed2-4aa9-8dc0-a80a1701848e\",\n" +
                "\t\"toUserId\": \"c89eb9dd-11bc-4f38-ba65-f5574a376374\",\n" +
                "\t\"content\": \"я жду!\"\n" +
                "}";

        Long time = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();

        assertThrows(InvalidDataException.class, () -> {
            notificationServiceImp.addNotification(message, time);
        });
    }

    @Test
    @DisplayName("add user notification should throw exception")
    void given_invalidNotificFormat_when_addNotification_then_addUserNotification() {
        UserNotificationSettingEntity userNotificationSettingEntity =
                UserNotificationSettingFactory.getUserNotificationSettingEntity(settings);
        notificationSettingRepository.save(userNotificationSettingEntity);

        String message = "{\n" +
                "\t\"authorId\": \"cfe5f13b-eed2-4aa9-8dc0-a80a1701848e\",\n" +
                "\t\"toUserId\": \"c89eb9dd-11bc-4f38-ba65-f5574a376374\",\n" +
                "\t\"notificationType\": \"MESSAGE_MESSAGE\",\n" +
                "\t\"content\": \"я жду!\"\n" +
                "}";

        Long time = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();

        assertThrows(InvalidDataException.class, () -> {
            notificationServiceImp.addNotification(message, time);
        });
    }




}
