package ru.skillbox.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.skillbox.annotation.password.Password;


@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountRequest {

    @Email(message = "Введите корректное значение Email")
    private String email;

    @Password
    private String password1;

    @NotBlank(message = "Повторите пароль")
    private String password2;

    @NotBlank(message = "Имя должно быть заполнена")
    @Size(min = 2, max = 50, message = "Имя должно содежать от {min} до {max} символов")
    private String firstName;

    @NotBlank(message = "Фамилия должна быть заполнена")
    @Size(min = 2, max = 50, message = "Фамилия должна содежать от {min} до {max} символов")
    private String lastName;

    public AccountRequest(AccountRequest request) {
        this.email = request.getEmail();
        this.password1 = request.getPassword1();
        this.password2 = request.getPassword2();
        this.firstName = request.getFirstName();
        this.lastName = request.getLastName();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
