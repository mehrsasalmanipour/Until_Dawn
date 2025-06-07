package com.twentyminutestilldawn.controllers;

import com.twentyminutestilldawn.models.User;
import com.twentyminutestilldawn.models.UserDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScoreboardController {
    public enum SortMode { SCORE, USERNAME, KILL, SURVIVAL }

    public List<User> getTopUsers(SortMode mode) {
        List<User> users = new ArrayList<>(UserDatabase.getAllUsers().values());

        switch (mode) {
            case SCORE:
                users.sort(Comparator.comparingInt(User::getScore).reversed());
                break;
            case USERNAME:
                users.sort(Comparator.comparing(User::getUsername));
                break;
            case KILL:
                users.sort(Comparator.comparingInt(User::getTotalKills).reversed());
                break;
            case SURVIVAL:
                users.sort(Comparator.comparingDouble(User::getTotalSurvivalTime).reversed());
                break;
        }

        return users.subList(0, Math.min(10, users.size()));
    }
}
