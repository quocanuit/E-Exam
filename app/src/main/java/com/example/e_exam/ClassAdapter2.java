package com.example.e_exam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ClassAdapter2 extends RecyclerView.Adapter<ClassAdapter2.ClassViewHolder> {

    private ArrayList<String> classList;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnItemLongClickListener longClickListener;
    private DatabaseReference databaseRef;

    public ClassAdapter2 (ArrayList<String> classList) {
        this.classList = classList;
        this.databaseRef = FirebaseDatabase.getInstance().getReference("Classes");
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String className = classList.get(position);
        holder.tvClassName.setText(className);
        holder.iconCourse.setImageResource(R.drawable.course);

        // Get teacher name from Firebase
        databaseRef.child(className).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String teacherName = snapshot.child("teacherName").getValue(String.class);
                if (teacherName != null && !teacherName.isEmpty()) {
                    holder.tvTeacherName.setText(teacherName);
                } else {
                    holder.tvTeacherName.setText("Chưa có giáo viên");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.tvTeacherName.setText("Chưa có giáo viên");
            }
        });

        // Set selected background color
        holder.itemView.setBackgroundColor(selectedPosition == position ?
                Color.parseColor("#E1BEE7") : Color.TRANSPARENT);

        // Handle click event
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();

            // Navigate to ListStudentActivity
            Intent intent = new Intent(holder.itemView.getContext(), ClassActivity.class);
            intent.putExtra("CLASS_NAME", className);
            holder.itemView.getContext().startActivity(intent);
        });

        // Handle long click
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(className);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView tvClassName;
        TextView tvTeacherName;
        ImageView iconCourse;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvTeacherName = itemView.findViewById(R.id.tvTeacherName);
            iconCourse = itemView.findViewById(R.id.icon_course);
        }
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(String className);
    }
}
