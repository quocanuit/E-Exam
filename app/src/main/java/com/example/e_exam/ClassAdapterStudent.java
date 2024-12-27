package com.example.e_exam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private String studentId;

    public interface OnClassJoinListener {
        void onClassJoin(String className, String studentName);
    }

    public ClassAdapterStudent(ArrayList<String> classList, Context context, OnClassJoinListener listener, String studentId) {
        this.classList = classList;
        this.context = context;
        this.listener = listener;
        this.studentId = studentId;
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
            DatabaseReference classRef = FirebaseDatabase.getInstance()
                    .getReference("Classes")
                    .child(className)
                    .child("students")
                    .child(studentId);

            classRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Nếu sinh viên đã tham gia lớp
                        Intent intent = new Intent(context, ClassStudent.class);
                        intent.putExtra("className", className);
                        context.startActivity(intent);
                    } else {
                        // Nếu sinh viên chưa tham gia lớp
                        fetchStudentNameAndShowDialog(className);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void fetchStudentNameAndShowDialog(String className) {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(studentId)
                .child("fullName");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String studentName = snapshot.getValue(String.class);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Bạn có muốn tham gia lớp này không?");
                    builder.setMessage("Tên sinh viên: " + studentName);

                    builder.setPositiveButton("Tham gia", (dialog, which) -> {
                        listener.onClassJoin(className, studentName);
                        dialog.dismiss();
                    });

                    builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
                    builder.show();
                } else {
                    Toast.makeText(context, "Không tìm thấy thông tin sinh viên.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
