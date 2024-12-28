package com.example.e_exam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ClassAdapterStudent extends RecyclerView.Adapter<ClassAdapterStudent.ClassViewHolder> {

    private ArrayList<String> classList;
    private Context context;
    private OnClassJoinListener listener;
    private String studentId; // Thêm biến này để lưu UID của sinh viên

    public interface OnClassJoinListener {
        void onClassJoin(String className, String studentName);
    }

    public ClassAdapterStudent(ArrayList<String> classList, Context context, OnClassJoinListener listener, String studentId) {
        this.classList = classList;
        this.context = context;
        this.listener = listener;
        this.studentId = studentId; // Khởi tạo biến studentId
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
            if (className != null && !className.isEmpty() && studentId != null && !studentId.isEmpty()) {
                // Kiểm tra xem sinh viên đã tham gia lớp này chưa
                DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("Classes").child(className).child("students").child(studentId);
                classRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Nếu sinh viên đã tham gia lớp, chuyển đến ClassStudent
                            Intent intent = new Intent(context, ClassStudent.class);
                            intent.putExtra("className", className);
                             intent.putExtra("StudentId", studentId);
                            context.startActivity(intent);
                        } else {
                            // Nếu sinh viên chưa tham gia lớp, hiển thị dialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Bạn có muốn tham gia lớp này không?");
                            builder.setMessage("Nhập tên của bạn:");
                            // Thêm EditText để nhập tên sinh viên
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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(context, "Class name or student ID is invalid.", Toast.LENGTH_SHORT).show();
            }
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
