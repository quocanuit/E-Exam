package com.example.e_exam;

public class Class {
    private String className; // Tên lớp học
    private String teacherName;
    // Constructor không tham số (Firebase yêu cầu constructor mặc định)
    public Class() {
        // Constructor mặc định là cần thiết khi Firebase muốn tạo đối tượng từ dữ liệu JSON
    }

    // Constructor với tham số để khởi tạo tên và mã lớp học
    public Class(String className, String teacherName) {
        this.className = className;
        this.teacherName = teacherName;
    }

    // Getter và Setter cho className
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    // Getter và Setter cho classId

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
