package com.example.e_exam;

public class Assignment {
    private String name;

    public Assignment() {
        // Default constructor required for calls to DataSnapshot.getValue(Assignment.class)
    }

    public Assignment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
