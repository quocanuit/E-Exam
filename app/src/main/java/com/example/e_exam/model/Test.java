package com.example.e_exam.model;

import java.util.List;

public class Test {
    private String name;
    private String testClass;
    private List<Question> questions;

    public Test(String name, String testClass, List<Question> questions) {
        this.name = name;
        this.testClass = testClass;
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public String getTestClass() {
        return testClass;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
