package ru.skillbox.dto.setting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserNotificationSettingsDto {
    private String id;
    @NotBlank(message = "userId is required field")
    private String userId;
    @NotNull(message = "friendRequest is required field")
    private Boolean friendRequest;
    @NotNull(message = "friendBirthday is required field")
    private Boolean friendBirthday;
    @NotNull(message = "postComment is required field")
    private Boolean postComment;
    @NotNull(message = "commentComment is required field")
    private Boolean commentComment;
    @NotNull(message = "post is required field")
    private Boolean post;
    @NotNull(message = "message is required field")
    private Boolean message;
    @NotNull(message = "sendPhoneMessage is required field")
    private Boolean sendPhoneMessage;
    @NotNull(message = "sendEmailMessage is required field")
    private Boolean sendEmailMessage;
}
