package com.example.e_exam.model;

import java.io.Serializable;

public class Answer implements Serializable {
    private String id;
    private String correctAnswer;
    private String selectedAnswer;

    public Answer() {};

    public Answer(String id, String correctAnswer, String selectedAnswer) {
        this.id = id;
        this.correctAnswer = correctAnswer;
        this.selectedAnswer = selectedAnswer;
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

    public String getSelectedAnswer() { return selectedAnswer; }

    public void setSelectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer; }
}
