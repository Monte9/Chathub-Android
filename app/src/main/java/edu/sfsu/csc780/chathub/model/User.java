package edu.sfsu.csc780.chathub.model;

import java.util.Date;

/**
 * Created by montewithpillow on 12/3/16.
 */

public class User {
    private String name;
    private String email;
    private String profileImageUrl;
    private String nickname;
    private String phoneNumber;

    public User() {
    }

    public User(String name, String email, String profileImageUrl, String nickname, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() { return profileImageUrl; }

    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
