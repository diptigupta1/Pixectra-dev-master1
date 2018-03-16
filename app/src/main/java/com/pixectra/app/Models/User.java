package com.pixectra.app.Models;

/**
 * Created by XCODER on 2/10/2018.
 */

public class User {
    String name;
    String email;
    String profilePic;
    String phoneNo;

    public User() {
    }

    public User(String name, String email, String profilePic, String phoneNo) {
        this.name = name;
        this.email = email;
        this.profilePic = profilePic;
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                '}';
    }
}
