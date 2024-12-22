package com.example.e_exam;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
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

        // Đặt màu nền cho mục được chọn
        holder.itemView.setBackgroundColor(selectedPosition == position ? Color.parseColor("#E1BEE7") : Color.TRANSPARENT);

        // Xử lý sự kiện khi nhấn vào mục trong RecyclerView
        holder.itemView.setOnClickListener(v -> {
            // Lưu lại vị trí được chọn
            selectedPosition = position;

            // Cập nhật màu nền
            notifyDataSetChanged();

            // Chuyển đến ClassActivity
            Intent intent = new Intent(holder.itemView.getContext(), ClassActivity.class);
            intent.putExtra("CLASS_NAME", className);
            holder.itemView.getContext().startActivity(intent);
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
