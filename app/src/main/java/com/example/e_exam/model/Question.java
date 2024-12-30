package com.example.e_exam.model;

import java.util.Map;

public class Question {
    private String questionText;
    private Map<String, String> answers;
    private String[] options;
    private String correctAnswer;

    public Question(String questionText, Map<String, String> answers, String correctAnswer) {
        this.questionText = questionText;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    public Question(String questionText, String[] options) {
        this.questionText = questionText;
        this.options = options;
    }

    // Getters and setters
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }

    public String[] getOptions() { return options; }

    public void setOptions(String[] options) { this.options = options; }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
