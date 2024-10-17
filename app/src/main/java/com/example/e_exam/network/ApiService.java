package com.example.e_exam.network;

import com.example.e_exam.model.TeacherExamList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("list_teacher_exam")
    Call<List<TeacherExamList>> getExamList();
}