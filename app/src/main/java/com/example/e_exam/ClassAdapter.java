package com.example.e_exam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;
import android.content.Intent;
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
        // Set icon if needed
        holder.iconCourse.setImageResource(R.drawable.course); // Replace with your icon
        holder.itemView.setBackgroundColor(selectedPosition == position ? Color.parseColor("#E1BEE7") : Color.TRANSPARENT);

        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPosition);
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition);

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
