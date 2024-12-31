package com.example.e_exam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private List<Score> scoreList;

    public ScoreAdapter(List<Score> scoreList) {
        this.scoreList = scoreList;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        Score score = scoreList.get(position);
        holder.classNameTextView.setText(score.getClassName());
        holder.studentNameTextView.setText(score.getStudentName());
        holder.scoreAverageTextView.setText(String.valueOf(score.getScoreAverage()));
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    static class ScoreViewHolder extends RecyclerView.ViewHolder {

        TextView classNameTextView;
        TextView studentNameTextView;
        TextView scoreAverageTextView;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            classNameTextView = itemView.findViewById(R.id.class_name_text_view);
            studentNameTextView = itemView.findViewById(R.id.student_name_text_view);
            scoreAverageTextView = itemView.findViewById(R.id.score_average_text_view);
        }
    }
}
