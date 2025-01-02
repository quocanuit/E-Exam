package com.example.e_exam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter2 extends RecyclerView.Adapter<StudentAdapter2.ViewHolder> {

    private List<Student2> studentList;
    private Context context;

    public StudentAdapter2 (Context context, List<Student2> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student2 student = studentList.get(position);
        holder.bind(student);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TableLayout tableLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tableLayout = itemView.findViewById(R.id.table_layout);
        }

        public void bind(Student2 student) {
            // Clear existing rows
            tableLayout.removeAllViews();

            // Add header row
            TableRow headerRow = new TableRow(context);
            TextView studentIdHeader = new TextView(context);
            studentIdHeader.setText("Mã học sinh");
            studentIdHeader.setPadding(8, 8, 8, 8);
            studentIdHeader.setTextSize(16);
            studentIdHeader.setTextColor(context.getResources().getColor(android.R.color.white));
            studentIdHeader.setBackgroundColor(context.getResources().getColor(R.color.purple_700));
            headerRow.addView(studentIdHeader);

            TextView subjectHeader = new TextView(context);
            subjectHeader.setText("Môn học");
            subjectHeader.setPadding(8, 8, 8, 8);
            subjectHeader.setTextSize(16);
            subjectHeader.setTextColor(context.getResources().getColor(android.R.color.white));
            subjectHeader.setBackgroundColor(context.getResources().getColor(R.color.purple_700));
            headerRow.addView(subjectHeader);

            TextView scoreHeader = new TextView(context);
            scoreHeader.setText("Điểm");
            scoreHeader.setPadding(8, 8, 8, 8);
            scoreHeader.setTextSize(16);
            scoreHeader.setTextColor(context.getResources().getColor(android.R.color.white));
            scoreHeader.setBackgroundColor(context.getResources().getColor(R.color.purple_700));
            headerRow.addView(scoreHeader);

            tableLayout.addView(headerRow);

            // Add data row
            TableRow dataRow = new TableRow(context);
            TextView studentIdTextView = new TextView(context);
            studentIdTextView.setText(student.getStudentId());
            studentIdTextView.setPadding(8, 8, 8, 8);
            studentIdTextView.setTextSize(16);
            studentIdTextView.setTextColor(context.getResources().getColor(android.R.color.black));
            studentIdTextView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            dataRow.addView(studentIdTextView);

            TextView subjectTextView = new TextView(context);
            subjectTextView.setText(student.getSubjectName());
            subjectTextView.setPadding(8, 8, 8, 8);
            subjectTextView.setTextSize(16);
            subjectTextView.setTextColor(context.getResources().getColor(android.R.color.black));
            subjectTextView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            dataRow.addView(subjectTextView);

            TextView scoreTextView = new TextView(context);
            scoreTextView.setText(String.valueOf(student.getScoreAverage()));
            scoreTextView.setPadding(8, 8, 8, 8);
            scoreTextView.setTextSize(16);
            scoreTextView.setTextColor(context.getResources().getColor(R.color.red));
            scoreTextView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            scoreTextView.setGravity(android.view.Gravity.END);
            dataRow.addView(scoreTextView);

            tableLayout.addView(dataRow);
        }
    }
}
