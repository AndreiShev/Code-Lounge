package ru.skillbox.controller.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import ru.skillbox.BaseTestClass;
import ru.skillbox.dto.setting.CreateUserNotificationSettingsDto;
import ru.skillbox.factory.UserNotificationSettingFactory;
import ru.skillbox.factory.notification.*;
import ru.skillbox.model.setting.NotificationType;
import ru.skillbox.model.setting.UserNotificationSettingEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WithMockUser(username = "bbcbd6aa-4145-4778-9855-bb053105b87c")
class NotificationControllerTest extends BaseTestClass {

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
    @DisplayName("getUserNotification should return user notification")
    @MethodSource("provideNotificationObjects")
    void when_getUserNotification_then_returnUserNotification(Notification notification) throws Exception {
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


    @ParameterizedTest
    @DisplayName("getUserNotificationCount should return count user notification")
    @MethodSource("provideNotificationObjects")
    void when_getUserNotificationCount_then_returnCountUserNotification(Notification notification) throws Exception {
        UserNotificationSettingEntity userNotificationSettingEntity =
                UserNotificationSettingFactory.getUserNotificationSettingEntity(settings);
        notificationSettingRepository.save(userNotificationSettingEntity);

        Random random = new Random();
        int j = random.nextInt(5);
        String message = objectMapper.writeValueAsString(notification);

        for (int i = 0; i < j; i++) {
            notificationServiceImp.addNotification(message, LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        }

        mockMvc
                .perform(get("/api/v1/notifications/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(String.valueOf(j)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }
}
