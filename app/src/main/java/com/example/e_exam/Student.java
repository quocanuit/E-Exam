package com.example.e_exam;

public class Student {
    private String studentId;
    private String studentName;
    private String email;
    private double score; // Thêm điểm số cho học sinh

    // Constructor với tất cả thông tin bao gồm điểm số
    public Student(String studentId, String studentName, String email) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.score = score;
    }

    // Getters và setters cho các thuộc tính
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
