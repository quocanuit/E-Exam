package com.example.e_exam;

import java.util.List;

public class Topic {
    private String id;
    private List<Question> questions;

    public Topic() {};

    public Topic(String id, List<Question> questions) {
        this.id = id;
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}