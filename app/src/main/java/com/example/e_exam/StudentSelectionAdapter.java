package com.example.e_exam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentSelectionAdapter extends RecyclerView.Adapter<StudentSelectionAdapter.StudentViewHolder> {
    private List<UserModel> studentList;
    private OnStudentSelectedListener listener;

    public StudentSelectionAdapter(List<UserModel> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        UserModel user = studentList.get(position);
        holder.textView.setText(user.getFullName() + " (" + user.getUid() + ")");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStudentSelected(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void setOnStudentSelectedListener(OnStudentSelectedListener listener) {
        this.listener = listener;
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    public interface OnStudentSelectedListener {
        void onStudentSelected(UserModel student);
    }
}

class UserModel {
    private String uid;
    private String fullName;
    private String email;
    private String role;
    private String birthday;

    public UserModel() {}

    public String getUid() { return uid; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getBirthday() { return birthday; }
}