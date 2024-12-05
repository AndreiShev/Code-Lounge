package ru.skillbox.controller.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import ru.skillbox.BaseTestClass;
import ru.skillbox.dto.setting.CreateUserNotificationSettingsDto;
import ru.skillbox.dto.setting.NotificationSettingsDto;
import ru.skillbox.dto.setting.UserNotificationSettingsDto;
import ru.skillbox.factory.UserNotificationSettingFactory;
import ru.skillbox.model.setting.NotificationType;
import ru.skillbox.model.setting.UserNotificationSettingEntity;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WithMockUser(username = "bbcbd6aa-4145-4778-9855-bb053105b87c")
class NotificationSettingControllerTest extends BaseTestClass {
    private CreateUserNotificationSettingsDto randomSettings;
    private ObjectMapper objectMapper = new ObjectMapper();
    private Random random = new Random();

    @BeforeEach
    void before() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        randomSettings = CreateUserNotificationSettingsDto.builder()
                .userId(username)
                .friendRequest(random.nextInt(2) == 0)
                .friendBirthday(random.nextInt(2) == 0)
                .postComment(random.nextInt(2) == 0)
                .commentComment(random.nextInt(2) == 0)
                .post(random.nextInt(2) == 0)
                .message(random.nextInt(2) == 0)
                .sendPhoneMessage(random.nextInt(2) == 0)
                .sendEmailMessage(random.nextInt(2) == 0)
                .build();
    }


    @Test
    @DisplayName("createUserNotificationSettings should create user settings")
    void when_createUserNotificationSettings_then_createUserSettings() throws Exception {
        MvcResult result = mockMvc
                .perform(
                        post("/api/v1/notifications/settings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(randomSettings))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        CreateUserNotificationSettingsDto savedSetting =
                objectMapper.readValue(result.getResponse().getContentAsString(), CreateUserNotificationSettingsDto.class);

        randomSettings.setId(savedSetting.getId());

        assertThat(objectMapper.writeValueAsString(savedSetting)).isEqualTo(objectMapper.writeValueAsString(randomSettings));
    }


    @Test
    @DisplayName("getUserNotificationSettings should return user settings")
    void when_getUserNotificationSettings_then_returnUserSetting() throws Exception {
        UserNotificationSettingEntity userNotificationSettingEntity =
                UserNotificationSettingFactory.getUserNotificationSettingEntity(randomSettings);

        UserNotificationSettingEntity savedSettings = notificationSettingRepository.save(userNotificationSettingEntity);
        UserNotificationSettingsDto savedSettingsDto = UserNotificationSettingFactory.getUserNotificationSettingsDto(savedSettings);

        MvcResult result = mockMvc
                .perform(get("/api/v1/notifications/settings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        UserNotificationSettingsDto gotSettingsDto =
                objectMapper.readValue(result.getResponse().getContentAsString(), UserNotificationSettingsDto.class);

        assertThat(objectMapper.writeValueAsString(savedSettingsDto)).isEqualTo(objectMapper.writeValueAsString(gotSettingsDto));
    }

    @ParameterizedTest
    @EnumSource(NotificationType.class)
    @DisplayName("updateUserNotificationSettings should update user settings")
    void when_updateUserNotificationSettings_then_updatedUserSettings(NotificationType type) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CreateUserNotificationSettingsDto defaultSettings = CreateUserNotificationSettingsDto.builder()
                .userId(username)
                .friendRequest(false)
                .friendBirthday(false)
                .postComment(false)
                .commentComment(false)
                .post(false)
                .message(false)
                .sendPhoneMessage(false)
                .sendEmailMessage(false)
                .build();

        UserNotificationSettingEntity userNotificationSettingEntity =
                UserNotificationSettingFactory.getUserNotificationSettingEntity(defaultSettings);

        notificationSettingRepository.save(userNotificationSettingEntity);

        MvcResult currentSetting = mockMvc.perform(get("/api/v1/notifications/settings")).andReturn();

        NotificationSettingsDto updateSetting = NotificationSettingsDto.builder()
                .enable(true)
                .notificationType(type)
                .build();

        mockMvc
                .perform(
                        put("/api/v1/notifications/settings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateSetting))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status[0].message").value(true))
                .andReturn();

        MvcResult newSetting = mockMvc.perform(get("/api/v1/notifications/settings")).andReturn();

        UserNotificationSettingsDto newSettingDto =
                objectMapper.readValue(newSetting.getResponse().getContentAsString(), UserNotificationSettingsDto.class);

        NotificationSettingsDto newSNotificationSettingsDto = newSettingDto.getData().stream()
                .filter(setting -> setting.getNotificationType().equals(type))
                .findAny().orElseThrow();

        assertThat(currentSetting.getResponse().getContentAsString()).
                isNotEqualTo(newSetting.getResponse().getContentAsString());

        assertThat(newSNotificationSettingsDto.getEnable()).isTrue();
    }
}
