package com.example.e_exam.model;

import java.util.List;

public class Topic {
    private String id;
    private List<Answer> questions;

    public Topic() {};

    public Topic(String id, List<Answer> questions) {
        this.id = id;
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Answer> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Answer> questions) {
        this.questions = questions;
    }
}