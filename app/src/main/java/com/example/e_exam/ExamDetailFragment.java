package com.example.e_exam;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import com.example.e_exam.model.Answer;
import com.example.e_exam.model.Question;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExamDetailFragment extends Fragment {
    private static final String ARG_CLASS_NAME = "className";
    private static final String ARG_NAME = "name";
    private static final String ARG_DUE_DATE = "dueDate";
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private static final int REQUEST_CODE_PICK_FILE = 1;
    private String answerUrl;
    private String fileLink;
    private PDFView pdfView;
    private List<Question> questionList;
    private QuestionMutipleChoiceAdapter questionMutipleChoiceAdapter;
    private RecyclerView rcvChooseAnswer;

    public static ExamDetailFragment newInstance(String className, String name, long dueDate, String fileUri, String answerUrl , int questionCount) {
        ExamDetailFragment fragment = new ExamDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CLASS_NAME, className);
        args.putString(ARG_NAME, name);
        args.putLong(ARG_DUE_DATE, dueDate);
        args.putString("fileUri", fileUri); // Duy trì URI file
        args.putInt("questionCount", questionCount); // Số lượng câu hỏi
        args.putString("answerUrl", answerUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_detail, container, false);

        if (getArguments() != null) {
            String pdfUrl = getArguments().getString("pdfUrl");
            answerUrl = getArguments().getString("answerUrl");

            Log.d(TAG, "Received PDF URL: " + pdfUrl);
            Log.d(TAG, "Received Answer URL: " + answerUrl);

            int questionCount = getArguments().getInt("questionCount", 40);

            TextView questionCountText = view.findViewById(R.id.questionCountText);
            if (questionCountText != null) {
                questionCountText.setText("Number of Questions: " + questionCount);
            }

            pdfView = view.findViewById(R.id.pdfView);
            if (pdfUrl != null && !pdfUrl.isEmpty()) {
                displayPdf(pdfUrl);
            } else {
                Log.e(TAG, "PDF URL is null or empty");
                Toast.makeText(getContext(), "PDF URL not found", Toast.LENGTH_SHORT).show();
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
            Log.d(TAG, "Answer URL: " + answerUrl); // Thêm logging
            if (answerUrl != null && !answerUrl.isEmpty()) {
                downloadAndCheckAnswers(answerUrl);
            } else {
                Log.e(TAG, "Answer URL is null or empty"); // Thêm logging
                Toast.makeText(getContext(), "Answer file not found", Toast.LENGTH_SHORT).show();
            }
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

    private void displayPdf(String pdfUrl) {
        Log.d(TAG, "PDF URL: " + pdfUrl);

        // Hiển thị loading indicator
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading PDF...");
        progressDialog.show();

        // Tạo request để download file
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(pdfUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Error loading PDF: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    getActivity().runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Failed to load PDF",
                                Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                // Đọc bytes từ response
                byte[] pdfBytes = response.body().bytes();

                // Hiển thị PDF trên UI thread
                getActivity().runOnUiThread(() -> {
                    try {
                        pdfView.fromBytes(pdfBytes)
                                .enableSwipe(true)
                                .swipeHorizontal(false)
                                .enableDoubletap(true)
                                .defaultPage(0)
                                .onLoad(nbPages -> {
                                    Log.d(TAG, "PDF loaded successfully. Pages: " + nbPages);
                                    progressDialog.dismiss();
                                })
                                .onError(t -> {
                                    Log.e(TAG, "Error displaying PDF", t);
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(),
                                            "Error displaying PDF: " + t.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                })
                                .load();
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e(TAG, "Error displaying PDF", e);
                        Toast.makeText(getContext(),
                                "Error displaying PDF: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void checkAnswers(Uri excelFileUri) {
        try {
            // Lấy câu trả lời của người dùng từ adapter
            Map<Integer, String> userAnswers = questionMutipleChoiceAdapter.getUserAnswers();
            List<Answer> results = new ArrayList<>();

            // Đọc file Excel đáp án
            InputStream inputStream = getActivity().getContentResolver().openInputStream(excelFileUri);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Duyệt qua từng dòng trong file Excel
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề

                String questionId = String.valueOf(row.getRowNum()); // Số thứ tự câu hỏi
                String correctAnswer = row.getCell(1).getStringCellValue(); // Đáp án đúng

                Answer answer = new Answer();
                answer.setId(questionId);
                answer.setCorrectAnswer(correctAnswer);

                // Lấy câu trả lời của người dùng (nếu có)
                String selectedAnswer = userAnswers.get(row.getRowNum() - 1);
                answer.setSelectedAnswer(selectedAnswer);

                results.add(answer);
            }

            workbook.close();
            inputStream.close();

            // Chuyển đến fragment kết quả
            ExamResultFragment resultFragment = new ExamResultFragment();
            Bundle args = new Bundle();
            args.putSerializable("results", new ArrayList<>(results));
            args.putString("examName", getArguments().getString(ARG_NAME, "Exam"));
            resultFragment.setArguments(args);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, resultFragment)
                    .addToBackStack(null)
                    .commit();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(),
                    "Error reading answers: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void downloadAndCheckAnswers(String answerUrl) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Checking answers...");
        progressDialog.show();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(answerUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    handleError(progressDialog, "Failed to download answer file");
                    return;
                }

                try {
                    Map<Integer, String> userAnswers = questionMutipleChoiceAdapter.getUserAnswers();
                    List<Answer> results = processAnswers(response.body().byteStream(), userAnswers);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String examId = getArguments().getString("examId");
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (examId != null && currentUser != null) {
                        // Chuyển đổi List<Answer> thành List<Map>
                        List<Map<String, Object>> resultMaps = new ArrayList<>();
                        for (Answer answer : results) {
                            Map<String, Object> answerMap = new HashMap<>();
                            answerMap.put("id", answer.getId());
                            answerMap.put("selectedAnswer", answer.getSelectedAnswer());
                            answerMap.put("correctAnswer", answer.getCorrectAnswer());
                            resultMaps.add(answerMap);
                        }

                        Map<String, Object> examResult = new HashMap<>();
                        examResult.put("results", resultMaps);
                        examResult.put("completedAt", System.currentTimeMillis());
                        examResult.put("score", calculateScore(results));
                        examResult.put("userId", currentUser.getUid());
                        examResult.put("examName", getArguments().getString("name"));

                        // Cập nhật trạng thái và lưu kết quả
                        db.collection("exams").document(examId)
                                .update("status", "completed")
                                .addOnSuccessListener(aVoid -> {
                                    db.collection("examResults")
                                            .document(examId + "_" + currentUser.getUid())
                                            .set(examResult)
                                            .addOnSuccessListener(documentReference -> {
                                                showResults(resultMaps, progressDialog);
                                            })
                                            .addOnFailureListener(e -> handleError(progressDialog,
                                                    "Error saving results: " + e.getMessage()));
                                })
                                .addOnFailureListener(e -> handleError(progressDialog,
                                        "Error updating exam status: " + e.getMessage()));
                    }
                } catch (Exception e) {
                    handleError(progressDialog, "Error processing answers: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                handleError(progressDialog, "Error downloading answer file: " + e.getMessage());
            }
        });
    }

    private void showResults(List<Map<String, Object>> resultMaps, ProgressDialog progressDialog) {
        requireActivity().runOnUiThread(() -> {
            progressDialog.dismiss();
            ExamResultFragment resultFragment = new ExamResultFragment();
            Bundle args = new Bundle();
            args.putSerializable("results", new ArrayList<>(resultMaps));
            args.putString("examName", getArguments().getString("name", "Exam"));
            resultFragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, resultFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void handleError(ProgressDialog progressDialog, String message) {
        requireActivity().runOnUiThread(() -> {
            progressDialog.dismiss();
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        });
    }

    private int calculateScore(List<Answer> results) {
        int correct = 0;
        for (Answer answer : results) {
            if (answer.getSelectedAnswer() != null &&
                    answer.getSelectedAnswer().equals(answer.getCorrectAnswer())) {
                correct++;
            }
        }
        return correct;
    }

    private List<Answer> processAnswers(InputStream inputStream, Map<Integer, String> userAnswers) throws IOException {
        List<Answer> results = new ArrayList<>();

        // Đọc file Excel
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua từng dòng trong file Excel
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề

            String questionId = String.valueOf(row.getRowNum()); // Số thứ tự câu hỏi
            String correctAnswer = row.getCell(1).getStringCellValue(); // Đáp án đúng

            Answer answer = new Answer();
            answer.setId(questionId);
            answer.setCorrectAnswer(correctAnswer);

            // Lấy câu trả lời của người dùng (nếu có)
            String selectedAnswer = userAnswers.get(row.getRowNum() - 1);
            answer.setSelectedAnswer(selectedAnswer);

            results.add(answer);
        }

        workbook.close();
        inputStream.close();

        return results;
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
