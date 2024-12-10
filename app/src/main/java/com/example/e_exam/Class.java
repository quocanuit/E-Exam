package com.example.e_exam;

public class Class {
    private String className; // Tên lớp học
    private String classId;   // Mã lớp học

    // Constructor không tham số (Firebase yêu cầu constructor mặc định)
    public Class() {
        // Constructor mặc định là cần thiết khi Firebase muốn tạo đối tượng từ dữ liệu JSON
    }

    // Constructor với tham số để khởi tạo tên và mã lớp học
    public Class(String className, String classId) {
        this.className = className;
        this.classId = classId;
    }

    // Getter và Setter cho className
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    // Getter và Setter cho classId
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }
}
