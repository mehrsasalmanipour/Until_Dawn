package com.twentyminutestilldawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class User {
    private String username;
    private String password;
    private String avatarPath;
    private String securityQuestion;
    private String securityAnswer;
    private int score;
    private int totalKills;
    private float totalSurvivalTime;

    public User() {}

    public User(String username, String password, String securityQuestion, String securityAnswer, String avatarPath) {
        this.username = username;
        this.password = password;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.avatarPath = avatarPath;
        this.score = 0;
        this.totalKills = 0;
        this.totalSurvivalTime = 0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isPasswordCorrect(String input) {
        return this.password.equals(input);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAvatarPath(String path) {
        this.avatarPath = path;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public Texture getAvatarTexture() {
        return new Texture(Gdx.files.internal("Avatar/" + avatarPath));
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public void addTotalKills(int totalKills) {
        this.totalKills += totalKills;
    }

    public float getTotalSurvivalTime() {
        return totalSurvivalTime;
    }

    public void addTotalSurvivalTime(float totalSurvivalTime) {
        this.totalSurvivalTime += totalSurvivalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User other = (User) o;
        return username.equals(other.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}