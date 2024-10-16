package com.example.e_exam;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.e_exam.adapter.TeacherExamListAdapter;
import com.example.e_exam.model.TeacherExamList;
import com.example.e_exam.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TeacherExamFragment extends Fragment {
    private RecyclerView recyclerView;
    private TeacherExamListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_exam, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchExamList();

        return view;
    }

    public void fetchExamList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://670e5e693e71518616543950.mockapi.io/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<TeacherExamList>> call = apiService.getExamList();

        call.enqueue(new Callback<List<TeacherExamList>>() {
            @Override
            public void onResponse(Call<List<TeacherExamList>> call, Response<List<TeacherExamList>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TeacherExamList> examList = response.body();
                    adapter = new TeacherExamListAdapter(examList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<TeacherExamList>> call, Throwable t) {

            }
        });
    }
}