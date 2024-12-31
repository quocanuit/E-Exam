package com.example.e_exam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentScoreAdapter extends RecyclerView.Adapter<StudentScoreAdapter.ScoreViewHolder> {

    private List<ScoreItem> scoreList;
    private OnScoreItemClickListener listener;
    private Context context;

    public interface OnScoreItemClickListener {
        void onScoreItemClick(ScoreItem score);
    }

    public StudentScoreAdapter(List<ScoreItem> scoreList, OnScoreItemClickListener listener, Context context) {
        this.scoreList = scoreList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        ScoreItem scoreItem = scoreList.get(position);
        holder.subjectName.setText(scoreItem.getSubjectName());

        holder.itemView.setOnClickListener(v -> listener.onScoreItemClick(scoreItem));

        // Set touch listener to change background color when pressed
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_500));
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName, score;
        ImageView iconGrades;

        ScoreViewHolder(View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.tv_subject_name);
            //iconGrades = itemView.findViewById(R.id.img_grades);
        }
    }
}
