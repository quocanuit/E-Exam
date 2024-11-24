package com.example.e_exam;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class StudentClassFragment extends Fragment {

    private ArrayList<String> classes;

    public StudentClassFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classes = new ArrayList<>();
        classes.add("A1");
        classes.add("A2");
        classes.add("A3");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_class, container, false);

        RecyclerView ds_lop = view.findViewById(R.id.ds_lop);
        ds_lop.setLayoutManager(new LinearLayoutManager(getContext()));

        ds_lop.setHasFixedSize(true);

        ClassAdapter classAdapter = new ClassAdapter(classes);
        ds_lop.setAdapter(classAdapter);

        return view;
    }
}