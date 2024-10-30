package com.example.e_exam;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.e_exam.adapter.StudentExamListAdapter;
import com.example.e_exam.model.StudentExamList;
import com.example.e_exam.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StudentExamFragment extends Fragment {
    private RecyclerView recyclerView;
    private StudentExamListAdapter adapter;
    private ApiService apiService;
    private List<StudentExamList> examList;
    private int currentDisplayedItems = 0;
    private static final int ITEMS_PER_PAGE = 20;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_exam, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StudentExamListAdapter();
        recyclerView.setAdapter(adapter);

        setupApiService();
        setupScrollListener();
        loadExams();

        return view;
    }

    private void setupApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://0d6ab1bc-40e9-45c1-8733-d29ffdab156a.mock.pstmn.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    private void setupScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount < examList.size()) {
                    loadMoreItems();
                }
            }
        });
    }

    private void loadExams() {
        Call<List<StudentExamList>> call = apiService.getStudentExamList();
        call.enqueue(new Callback<List<StudentExamList>>() {
            @Override
            public void onResponse(@NonNull Call<List<StudentExamList>> call, @NonNull Response<List<StudentExamList>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    examList = response.body();
                    loadMoreItems();
                } else {
                    showError("Unexpected response");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StudentExamList>> call, @NonNull Throwable t) {
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void loadMoreItems() {
        if (examList != null && currentDisplayedItems < examList.size()) {
            int endIndex = Math.min(currentDisplayedItems + ITEMS_PER_PAGE, examList.size());
            List<StudentExamList> newItems = examList.subList(currentDisplayedItems, endIndex);
            adapter.addExams(newItems);
            currentDisplayedItems = endIndex;
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}