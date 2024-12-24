package com.example.e_exam.model;

public class Questions {
    private String id;
    private String correctAnswer;

    public Questions() {};

    public Questions(String id, String correctAnswer) {
        this.id = id;
        this.correctAnswer = correctAnswer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
