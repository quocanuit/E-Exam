package com.example.e_exam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter2 extends RecyclerView.Adapter<StudentAdapter2.StudentViewHolder> {

    private List<Student2> studentList;

    public StudentAdapter2(List<Student2> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student2, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student2 student = studentList.get(position);

        // Kiểm tra xem TextView đã được gán chưa
        if (holder.studentIdTextView != null && holder.subjectTextView != null) {
            holder.studentIdTextView.setText(student.getStudentId());
            holder.subjectTextView.setText(student.getSubject());
            holder.scoreTextView.setText(String.valueOf(student.getScore()));
        }
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentIdTextView;
        TextView subjectTextView;
        TextView scoreTextView;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentIdTextView = itemView.findViewById(R.id.student_id_text_view);
            subjectTextView = itemView.findViewById(R.id.subject_text_view);
            scoreTextView = itemView.findViewById(R.id.score_text_view);
        }
    }
}