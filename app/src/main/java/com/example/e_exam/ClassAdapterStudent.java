package com.example.e_exam;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClassAdapterStudent extends RecyclerView.Adapter<ClassAdapterStudent.ClassViewHolder> {

    private ArrayList<String> classList;
    private Context context;
    private OnClassJoinListener listener;

    public interface OnClassJoinListener {
        void onClassJoin(String className, String studentName);
    }

    public ClassAdapterStudent(ArrayList<String> classList, Context context, OnClassJoinListener listener) {
        this.classList = classList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        String className = classList.get(position);
        holder.tvClassName.setText(className);
        holder.iconCourse.setImageResource(R.drawable.course);

        holder.itemView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Join Class");
            builder.setMessage("Do you want to join this class?");

            // Add an EditText field for student name input
            final EditText input = new EditText(context);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String studentName = input.getText().toString().trim();
                if (!studentName.isEmpty()) {
                    listener.onClassJoin(className, studentName);
                } else {
                    Toast.makeText(context, "Student name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView tvClassName;
        ImageView iconCourse;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            iconCourse = itemView.findViewById(R.id.icon_course);
        }
    }
}
