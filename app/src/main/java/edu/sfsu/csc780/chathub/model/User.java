package edu.sfsu.csc780.chathub.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by montewithpillow on 12/3/16.
 */

public class User implements Parcelable {
    private String name;
    private String email;
    private String profileImageUrl;
    private String nickname;
    private String phoneNumber;

    public User() {

    }

    public User(Parcel in) {
        String[] data = new String[5];

        in.readStringArray(data);
        this.name = data[0];
        this.email = data[1];
        this.profileImageUrl = data[2];
        this.nickname = data[3];
        this.phoneNumber = data[4];
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.name,
                this.email,
                this.profileImageUrl,
                this.phoneNumber,
                this.nickname
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
