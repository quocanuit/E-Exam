package com.example.e_exam.user;

public class UserInfor {
    private String email;
    private String birthday;
    private String hometown;
    private String class_activity;

    public UserInfor(String email, String birthday, String hometown, String class_activity) {
        this.email = email;
        this.birthday = birthday;
        this.hometown = hometown;
        this.class_activity = class_activity;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getClass_activity() {
        return class_activity;
    }

    public void setClass_activity(String class_activity) {
        this.class_activity = class_activity;
    }
}
