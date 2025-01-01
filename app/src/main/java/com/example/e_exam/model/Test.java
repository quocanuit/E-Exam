package com.example.e_exam.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Test {
    private String id;
    private String name;
    private String pdfUrl;
    private String status;
    private Long deadline; // Lưu dưới dạng timestamp (milliseconds)

    // Constructor mặc định
    public Test() {}

    // Constructor có tham số
    public Test(String id, String name, String pdfUrl, String status, Long deadline) {
        this.id = id;
        this.name = name;
        this.pdfUrl = pdfUrl;
        this.status = status;
        this.deadline = deadline;
    }

    // Getters và setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    // Tiện ích: Chuyển deadline thành Date
    public Date getDeadlineAsDate() {
        return deadline != null ? new Date(deadline) : null;
    }

    // Tiện ích: Kiểm tra bài kiểm tra đã hết hạn chưa
    public boolean isExpired() {
        return deadline != null && deadline < System.currentTimeMillis();
    }

    // Tiện ích: Định dạng deadline thành chuỗi (dd/MM/yyyy)
    public String getFormattedDeadline() {
        if (deadline == null) return "No Deadline";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(new Date(deadline));
    }
}
