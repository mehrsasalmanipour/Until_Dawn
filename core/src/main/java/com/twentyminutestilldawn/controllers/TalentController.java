package com.twentyminutestilldawn.controllers;

import com.twentyminutestilldawn.models.User;

public class TalentController {
    private final User user;

    public TalentController(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
