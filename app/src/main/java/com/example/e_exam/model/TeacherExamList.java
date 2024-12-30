package com.example.e_exam.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TeacherExamList {
    private String id;
    private String classCode;
    private String name;
    private long date;
    private boolean isAssigned;
    private String pdfUrl;

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

    public String getPdfUrl() {
        return pdfUrl;
    }

    public String getClassCode(){
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public TeacherExamList(){

    }

    public TeacherExamList(String name, long date, boolean isAssigned, String id) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.isAssigned = isAssigned;
    }

    public TeacherExamList(String classCode, String name, long date, boolean isAssigned, String id, String pdfUrl) {
        this.classCode = classCode;
        this.id = id;
        this.name = name;
        this.date = date;
        this.isAssigned = isAssigned;
        this.pdfUrl = pdfUrl;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return "Ngày tạo: " + sdf.format(new Date(date * 1000));
    }
}
