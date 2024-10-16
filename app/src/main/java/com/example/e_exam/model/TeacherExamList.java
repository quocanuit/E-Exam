package com.example.e_exam.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TeacherExamList {
    private String id;
    private String name;
    private long date;
    private boolean isAssigned;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getDate() {
        return date;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return "Created on: " + sdf.format(new Date(date * 1000));
    }
}
