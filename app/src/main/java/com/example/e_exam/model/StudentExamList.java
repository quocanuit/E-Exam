package com.example.e_exam.model;

import java.io.Serializable;  // Thêm import Serializable
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudentExamList implements Serializable {  // Thêm implements Serializable
    private String id;
    private String className;
    private String name;
    private String status;
    private long dueDate;
    private String pdfUrl;
    private String answerUrl;

    // Constructor
    public StudentExamList(String className, String name, String status, long dueDate, String id) {
        this.id = id;
        this.className = className;
        this.name = name;
        this.status = status;
        this.dueDate = dueDate;
    }

    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getAnswerUrl() {
        return answerUrl;
    }

    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }

    // Phương thức để hiển thị ngày tháng
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return "Deadline: " + sdf.format(new Date(dueDate * 1000));
    }
}
