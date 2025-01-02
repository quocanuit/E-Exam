package com.example.e_exam;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private List<Assignment> assignmentList;
    private String className;
    private Context context;

    public AssignmentAdapter(List<Assignment> assignmentList, String className, Context context) {
        this.assignmentList = assignmentList;
        this.className = className;
        this.context = context;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);
        holder.assignmentNameTextView.setText(assignment.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ListScoreStudentActivity.class);
            intent.putExtra("ASSIGNMENT_NAME", assignment.getName());
            intent.putExtra("CLASS_NAME", className);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    static class AssignmentViewHolder extends RecyclerView.ViewHolder {

        TextView assignmentNameTextView;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            assignmentNameTextView = itemView.findViewById(R.id.assignment_name_text_view);
        }
    }
}
