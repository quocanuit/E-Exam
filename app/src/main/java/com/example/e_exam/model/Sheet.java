package com.example.e_exam.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class Sheet {
    private String sheetId;
    private List<Topic> topics;

    public Sheet() {};

    public Sheet(String sheetId, List<Topic> topics) {
        this.sheetId = sheetId;
        this.topics = topics;
    }

    public String getSheetId() {
        return sheetId;
    }

    public void setSheetId(String sheetId) {
        this.sheetId = sheetId;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    // Phương thức để lưu Sheet vào Firebase
    public void createSheet() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Sheets");

        // Lưu trữ Sheet vào Firebase, sử dụng sheetId làm khóa
        myRef.child(sheetId).setValue(this);
    }
}