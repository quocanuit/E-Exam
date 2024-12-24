package com.example.e_exam.model;

import java.util.List;

public class Topic {
    private String id;
    private List<Questions> questions;

    public Topic() {};

    public Topic(String id, List<Questions> questions) {
        this.id = id;
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Questions> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Questions> questions) {
        this.questions = questions;
    }
}