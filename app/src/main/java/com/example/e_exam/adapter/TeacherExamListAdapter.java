package com.example.e_exam.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_exam.R;
import com.example.e_exam.model.TeacherExamList;

import java.util.List;

public class TeacherExamListAdapter extends RecyclerView.Adapter<TeacherExamListAdapter.TeacherExamListViewHolder> {
    private List<TeacherExamList> examList;

    public TeacherExamListAdapter(List<TeacherExamList> examList) {
        this.examList = examList;
    }

    @NonNull
    @Override
    public TeacherExamListAdapter.TeacherExamListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_teacher_exam, parent, false);
        return new TeacherExamListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherExamListAdapter.TeacherExamListViewHolder holder, int position) {
        TeacherExamList exam = examList.get(position);
        holder.nameTextView.setText(exam.getName());
        holder.dateTextView.setText(exam.getFormattedDate());
        holder.statusTextView.setText(exam.isAssigned() ? "Assigned" : "Unassigned");
        holder.statusTextView.setTextColor(exam.isAssigned() ?
                holder.itemView.getContext().getColor(R.color.green) :
                holder.itemView.getContext().getColor(R.color.on_surface));
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    static class TeacherExamListViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView dateTextView;
        TextView statusTextView;

        public TeacherExamListViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }
}
