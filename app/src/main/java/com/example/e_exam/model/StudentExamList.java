package com.example.e_exam.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudentExamList {
    private String id;
    private String className;
    private String name;
    private String status;
    private long dueDate;

    public String getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public StudentExamList(String className, String name, String status, long dueDate, String id) {
        this.id = id;
        this.className = className;
        this.name = name;
        this.status = status;
        this.dueDate = dueDate;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return "Deadline: " + sdf.format(new Date(dueDate * 1000));
    }
}

