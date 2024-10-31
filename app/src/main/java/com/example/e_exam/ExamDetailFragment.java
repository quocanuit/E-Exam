// ExamDetailFragment.java
package com.example.e_exam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExamDetailFragment extends Fragment {
    private static final String ARG_CLASS_NAME = "className";
    private static final String ARG_NAME = "name";
    private static final String ARG_DUE_DATE = "dueDate";

    public static ExamDetailFragment newInstance(String className, String name, long dueDate) {
        ExamDetailFragment fragment = new ExamDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CLASS_NAME, className);
        args.putString(ARG_NAME, name);
        args.putLong(ARG_DUE_DATE, dueDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_detail, container, false);

        if (getArguments() != null) {
            TextView classNameText = view.findViewById(R.id.className);
            TextView nameText = view.findViewById(R.id.name);
            TextView dueDateText = view.findViewById(R.id.dueDate);

            String className = getArguments().getString(ARG_CLASS_NAME);
            String name = getArguments().getString(ARG_NAME);
            long dueDate = getArguments().getLong(ARG_DUE_DATE);

            classNameText.setText(className);
            nameText.setText(name);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(new Date(dueDate * 1000L));
            dueDateText.setText(String.format("Deadline: %s", formattedDate));
        }

        return view;
    }
}