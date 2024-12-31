package com.example.e_exam;

public class Student2 {

    private String studentId;
    private String subjectName;
    private double score;

    public Student2(String studentId, String subjectName, double score) {
        this.studentId = studentId;
        this.subjectName = subjectName;
        this.score = score;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSubject() {
        return subjectName;
    }

    public void setSubject(String subjectName) {
        this.subjectName = subjectName;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
