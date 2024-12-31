package com.example.e_exam;

public class Score {
    private String className;
    private String studentName;
    private double scoreAverage;

    public Score(String className, String studentName, double scoreAverage) {
        this.className = className;
        this.studentName = studentName;
        this.scoreAverage = scoreAverage;
    }

    public String getClassName() {
        return className;
    }

    public String getStudentName() {
        return studentName;
    }

    public double getScoreAverage() {
        return scoreAverage;
    }
}
