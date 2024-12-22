package com.example.e_exam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ClassStudentsAdapter extends RecyclerView.Adapter<ClassStudentsAdapter.StudentViewHolder> {

    private ArrayList<String> studentsList;

    public ClassStudentsAdapter(ArrayList<String> studentsList) {
        this.studentsList = studentsList;
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentViewHolder holder, int position) {
        String studentName = studentsList.get(position);
        holder.studentNameTextView.setText(studentName);
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentNameTextView;

        public StudentViewHolder(View itemView) {
            super(itemView);
            studentNameTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
