package com.example.e_exam;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_exam.adapter.QuestionMutipleChoiceAdapter;
import com.example.e_exam.model.Question;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExamDetailFragment extends Fragment {
    private static final String ARG_CLASS_NAME = "className";
    private static final String ARG_NAME = "name";
    private static final String ARG_DUE_DATE = "dueDate";
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private static final int REQUEST_CODE_PICK_FILE = 1;
    private String fileLink;
    private PDFView pdfView;
    private List<Question> questionList;
    private QuestionMutipleChoiceAdapter questionMutipleChoiceAdapter;
    private RecyclerView rcvChooseAnswer;

    public static ExamDetailFragment newInstance(String className, String name, long dueDate, String fileUri, int questionCount) {
        ExamDetailFragment fragment = new ExamDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CLASS_NAME, className);
        args.putString(ARG_NAME, name);
        args.putLong(ARG_DUE_DATE, dueDate);
        args.putString("fileUri", fileUri); // Duy trì URI file
        args.putInt("questionCount", questionCount); // Số lượng câu hỏi
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_detail, container, false);

        if (getArguments() != null) {
            fileLink = getArguments().getString("fileUri");
            int questionCount = getArguments().getInt("questionCount", 0);

            TextView questionCountText = view.findViewById(R.id.questionCountText);
            if (questionCountText != null) {
                questionCountText.setText("Number of Questions: " + questionCount);
            }

            pdfView = view.findViewById(R.id.pdfView);
            if (fileLink != null) {
                displayPdf(fileLink); // Hiển thị PDF nếu có fileLink
            }

            rcvChooseAnswer = view.findViewById(R.id.rcvChooseAnswer);
            rcvChooseAnswer.setLayoutManager(new LinearLayoutManager(getContext()));
            questionList = new ArrayList<>();
            for (int i = 1; i <= 40; i++) {
                questionList.add(new Question("What is the correct answer to question " + i + "?",
                        new String[] {"A", "B", "C", "D"}));
            }

            questionMutipleChoiceAdapter = new QuestionMutipleChoiceAdapter(questionList, new QuestionMutipleChoiceAdapter.OnAnswerSelectedListener() {
                @Override
                public void onAnswerSelected(int position, String selectedAnswer) {
                    // Xử lý khi người dùng chọn đáp án
                }
            });
            rcvChooseAnswer.setAdapter(questionMutipleChoiceAdapter);
        }

        Button submitButton = view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> {
            // Tạo fragment mới (ví dụ: ExamResultFragment)
            ExamResultFragment resultFragment = new ExamResultFragment();

            // Gửi dữ liệu (nếu cần) vào fragment mới
            Bundle args = new Bundle();
            args.putInt("score", 0);  // Ví dụ: đưa điểm số vào
            resultFragment.setArguments(args);

            // Thực hiện chuyển fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, resultFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri fileUri = data.getData();
                checkAnswers(fileUri);
            }
        }
    }

    private void displayPdf(String fileLink) {
        Log.d(TAG, "File URI: " + fileLink);

        try {
            InputStream inputStream = getContext().getAssets().open(fileLink);
            if (inputStream != null) {
                pdfView.fromStream(inputStream)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .defaultPage(0)
                        .onLoad(nbPages -> Log.d(TAG, "PDF loaded successfully. Pages: " + nbPages))
                        .onError(throwable -> {
                            Log.e(TAG, "Error loading PDF", throwable);
                            Toast.makeText(getContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        })
                        .load();
            } else {
                Log.e(TAG, "Failed to open PDF from assets.");
                Toast.makeText(getContext(), "Failed to open PDF", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException when opening PDF", e);
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error", e);
            Toast.makeText(getContext(), "Unexpected error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void checkAnswers(Uri fileName) {
        try {
            int score = 0;
            // Hiển thị điểm số hoặc thực hiện hành động khác
            Toast.makeText(getContext(), "Your score: " + score + "/" + questionList.size(), Toast.LENGTH_LONG).show();
            navigateToResultFragment(score);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error reading answers: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToResultFragment(int score) {
        ExamResultFragment resultFragment = new ExamResultFragment();
        Bundle args = new Bundle();
        args.putInt("score", score);
        resultFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, resultFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayPdf(fileLink); // Thực hiện mở tài liệu khi quyền đã được cấp
            } else {
                Toast.makeText(getContext(), "Permission denied. Cannot open PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
