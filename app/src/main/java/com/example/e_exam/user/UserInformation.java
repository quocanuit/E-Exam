package com.example.e_exam.user;

public class UserInformation {
    private String name;
    private String email;
    private String birthday;
    private String id;
    private String hometown;
    private String class_activity;
    private boolean isTeacher;

    public UserInformation(String name, String id, String email, String birthday, String class_activity, String hometown, Boolean isTeacher) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.birthday = birthday;
        this.class_activity = class_activity;
        this.hometown = hometown;
        this.isTeacher = isTeacher;
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

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

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

    public boolean isTeacher() {
        return isTeacher;
    }

    public void setTeacher(boolean teacher) {
        isTeacher = teacher;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
