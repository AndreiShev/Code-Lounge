package ru.skillbox.dto.requests;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.skillbox.annotation.password.Password;

@AllArgsConstructor
@NoArgsConstructor
public class RecoveryRequest {

    @Password
    private String temp;

    @Email(message = "Введите корректное значение Email")
    private String email;

    public RecoveryRequest(RecoveryRequest recoveryRequest) {
        this.temp = recoveryRequest.getTemp();
        this.email = recoveryRequest.getEmail();
    }


    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}