package by.bsuir.client.utils;

import by.bsuir.client.models.User;

public class UserValidator {
    public static boolean isValidUserDataForRegister(User user) {
        return user.getLogin() != null && user.getPassword() != null && user.getPersonData() != null
                && user.getPersonData().getFirstName() != null && user.getRole() != null;
    }
}
