package ru.skillbox.mapper;

import lombok.experimental.UtilityClass;
import ru.skillbox.dto.AccountDto;
import ru.skillbox.entity.Account;

@UtilityClass
public class AccountUpdateFactory {

    public static void updateFields(Account updatedAccount, AccountDto accountDto) {

        if (accountDto.getCity() != null) {
            String city = accountDto.getCity();
            updatedAccount.setCity(city.isBlank() ? null : city);
        }
        if (accountDto.getCountry() != null) {
            String country = accountDto.getCountry();
            updatedAccount.setCountry(country.isBlank() ? null : country);
        }
        if (accountDto.getFirstName() != null && !accountDto.getFirstName().isEmpty()) {
            updatedAccount.setFirstName(accountDto.getFirstName().trim().toUpperCase());
        }
        if (accountDto.getLastName() != null && !accountDto.getLastName().isEmpty()) {
            updatedAccount.setLastName(accountDto.getLastName().trim().toUpperCase());
        }
        if (accountDto.getBirthDate() == null) {
            updatedAccount.setBirthDate(null);
        }
        if (accountDto.getBirthDate() != null) {
            updatedAccount.setBirthDate(accountDto.getBirthDate());
        }

        if (accountDto.getEmojiStatus() != null) {
            updatedAccount.setEmojiStatus(accountDto.getEmojiStatus());
        }
        if (accountDto.getPhone() != null) {
            String phone = accountDto.getPhone();
            if (phone.isBlank()) {
                updatedAccount.setPhone(null);
            }
            if (phone.length() == 11 && phone.startsWith("7")) {
                updatedAccount.setPhone(phone);
            }
            if (phone.length() == 10) {
                updatedAccount.setPhone("7" + phone);
            }
        }
        if (accountDto.getAbout() != null) {
            String about = accountDto.getAbout();
            updatedAccount.setAbout(about.isBlank() ? null : about);
        }
        if (accountDto.getPhoto() != null) {
            String photo = accountDto.getPhoto();
            updatedAccount.setPhoto(photo.isBlank() ? null : photo);
        }
        if (accountDto.getProfileCover() != null) {
            String profileCover = accountDto.getProfileCover();
            updatedAccount.setProfileCover(profileCover.isBlank() ? null : profileCover);
        }
    }
}
