package com.example.e_exam;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private ArrayList<String> classList;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public ClassAdapter(ArrayList<String> classList) {
        this.classList = classList;
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
        holder.iconCourse.setImageResource(R.drawable.course); // Replace with your icon

        // Đặt màu nền trắng ban đầu
        holder.itemView.setBackgroundColor(selectedPosition == position ? Color.parseColor("#E1BEE7") : Color.TRANSPARENT);

        holder.itemView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Khi nhấn xuống, thay đổi màu nền
                    v.setBackgroundColor(Color.parseColor("#E1BEE7"));
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Khi thả tay, quay về màu trắng ban đầu
                    v.setBackgroundColor(Color.TRANSPARENT);

                    // Xử lý sự kiện chuyển đến ClassActivity khi người dùng nhấn xong
                    Intent intent = new Intent(holder.itemView.getContext(), ClassActivity.class);
                    intent.putExtra("CLASS_NAME", className);
                    holder.itemView.getContext().startActivity(intent);
                    return true;
            }
            return false;
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
