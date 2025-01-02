package com.example.e_exam;

public class Student2 {
    private String studentId;
    private String subjectName;
    private double scoreAverage;

    public Student2(String studentId, String subjectName, double scoreAverage) {
        this.studentId = studentId;
        this.subjectName = subjectName;
        this.scoreAverage = scoreAverage;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public double getScoreAverage() {
        return scoreAverage;
    }
}
