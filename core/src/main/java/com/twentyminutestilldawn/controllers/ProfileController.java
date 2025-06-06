package com.twentyminutestilldawn.controllers;

import com.twentyminutestilldawn.models.User;
import com.twentyminutestilldawn.models.UserDatabase;
import com.twentyminutestilldawn.views.ProfileMenu;

public class ProfileController {
    private ProfileMenu view;

    private final User user;

    public void setView(ProfileMenu view) {
        this.view = view;
    }

    public ProfileController(User user) {
        this.user = user;
    }

    public boolean updateUsername(String newUsername) {
        if (newUsername.equals(user.getUsername())) {
            return false;
        }

        if (UserDatabase.userExists(newUsername)) {
            return false;
        }

        UserDatabase.deleteUser(user.getUsername());
        user.setUsername(newUsername);
        UserDatabase.registerUser(user);
        return true;
    }

    public boolean updatePassword(String newPassword) {
        if (newPassword == null || !isStrongPassword(newPassword)) {
            return false;
        }

        user.setPassword(newPassword.trim());
        UserDatabase.registerUser(user);
        return true;
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[@#$%^&+=!()_.*].*");
    }

    public void updateAvatar(String avatarPath) {
        user.setAvatarPath(avatarPath);
        UserDatabase.registerUser(user);
    }

    public void deleteAccount() {
        UserDatabase.deleteUser(user.getUsername());
    }
}
