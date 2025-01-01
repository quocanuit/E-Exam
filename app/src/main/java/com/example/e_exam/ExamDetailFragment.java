package com.example.e_exam;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_exam.adapter.QuestionMutipleChoiceAdapter;
import com.example.e_exam.model.Answer;
import com.example.e_exam.model.Question;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    private String examId;
    private FirebaseFirestore db;
    private View mView;
    private TextView classNameText, nameTestText, timerDueText, numberOfQuestionsText;
    private Button submitButton;
    private String pdfUrl;
    private String answerUrl;
    private String fileLink;
    private PDFView pdfView;
    private List<Question> questionList;
    private QuestionMutipleChoiceAdapter questionMutipleChoiceAdapter;
    private RecyclerView rcvChooseAnswer;

    public static ExamDetailFragment newInstance(String examId, String className, String name, long dueDate, String pdfUrl, String answerUrl) {
        ExamDetailFragment fragment = new ExamDetailFragment();
        Bundle args = new Bundle();
        args.putString("id", examId);
        args.putString(ARG_CLASS_NAME, className);
        args.putString(ARG_NAME, name);
        args.putLong(ARG_DUE_DATE, dueDate);
        args.putString("pdfUrl", pdfUrl);
        args.putString("answerUrl", answerUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_exam_detail, container, false);

        initUI();

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            examId = getArguments().getString("id");
            pdfUrl = getArguments().getString("pdfUrl");
            answerUrl = getArguments().getString("answerUrl");
            loadExamDetailsFromFireStore(pdfUrl);
        }

        onClickListener();

        return mView;
    }

    private void initUI(){
        classNameText = mView.findViewById(R.id.classNameText);
        nameTestText = mView.findViewById(R.id.nameTestText);
        numberOfQuestionsText = mView.findViewById(R.id.numberOfQuestionsText);
        pdfView = mView.findViewById(R.id.pdfView);
        timerDueText = mView.findViewById(R.id.timerDueText);
        rcvChooseAnswer = mView.findViewById(R.id.rcvChooseAnswer);
        submitButton = mView.findViewById(R.id.submitButton);
    }

    private void onClickListener(){
        submitButton.setOnClickListener(v -> {
            Log.d(TAG, "Answer URL: " + answerUrl); // Thêm logging
            if (answerUrl != null && !answerUrl.isEmpty()) {
                downloadAndCheckAnswers(answerUrl);
            } else {
                Log.e(TAG, "Answer URL is null or empty"); // Thêm logging
                Toast.makeText(getContext(), "Answer file not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadExamDetailsFromFireStore(String pdfUrl) {
        Log.d(TAG, "PDF URL: " + pdfUrl);
        if (examId == null || examId.isEmpty()) {
            Toast.makeText(getContext(), "Exam ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("exams").document(examId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        updateUIWithFireStoreData(documentSnapshot);
                    } else {
                        Toast.makeText(getContext(), "Exam not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading exam details: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void updateUIWithFireStoreData(DocumentSnapshot documentSnapshot) {
        String className = documentSnapshot.getString("className");
        String testName = documentSnapshot.getString("name");

        Long dueDate = documentSnapshot.getLong("deadline");
        Long timeLimit = documentSnapshot.getLong("timeLimit");

        int numberOfQuestions = documentSnapshot.getLong("numberOfQuestions").intValue();
        String pdfUrl = documentSnapshot.getString("pdfUrl");
        answerUrl = documentSnapshot.getString("answerUrl");

        classNameText.setText(className != null ? className : "N/A");
        nameTestText.setText(testName != null ? testName : "N/A");
        numberOfQuestionsText.setText(String.valueOf(numberOfQuestions));

        if (pdfUrl != null && !pdfUrl.isEmpty()) {
            downloadAndDisplayPdf(pdfUrl);
        } else {
            Toast.makeText(getContext(), "No PDF URL provided", Toast.LENGTH_SHORT).show();
        }

        if (dueDate > System.currentTimeMillis()) {
            countDownTimer(timeLimit);
        } else {
            timerDueText.setText("Expired");
        }

        displayChooseAnswer(numberOfQuestions); // Hiển thị danh sách câu hỏi dựa trên số lượng
    }

    private void displayChooseAnswer(int numberOfQuestions) {
        rcvChooseAnswer.setLayoutManager(new LinearLayoutManager(getContext()));
        questionList = new ArrayList<>();
        for (int i = 1; i <= numberOfQuestions; i++) {
            questionList.add(new Question("Câu " + i + ": Chọn câu trả lời đúng",
                    new String[]{"A", "B", "C", "D"}));
        }

        questionMutipleChoiceAdapter = new QuestionMutipleChoiceAdapter(questionList, (position, selectedAnswer) -> {
            // Xử lý khi người dùng chọn đáp án
        });
        rcvChooseAnswer.setAdapter(questionMutipleChoiceAdapter);
    }

    private void countDownTimer(long timeLimit) {
        long remainingTimeMillis = timeLimit * 60 * 1000;

        if (remainingTimeMillis > 0) {
            new CountDownTimer(remainingTimeMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;

                    String timeFormatted = String.format("%02d:%02d", minutes, seconds);
                    timerDueText.setText(timeFormatted);
                }

                @Override
                public void onFinish() {
                    timerDueText.setText("Time's up!");
                    submitButton.setEnabled(false);

                    // Hiển thị ProgressDialog
                    ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Đã hết giờ làm bài. Vui lòng nộp bài!");
                    progressDialog.setCancelable(false);
                    progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Nộp bài", (dialog, which) -> {
                        dialog.dismiss();
                        downloadAndCheckAnswers(answerUrl);
                    });
                    progressDialog.show();
                }
            }.start();
        } else {
            timerDueText.setText("Expired");
            submitButton.setEnabled(false);
        }
    }


    private void downloadAndDisplayPdf(String pdfUrl) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading PDF...");
        progressDialog.show();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(pdfUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Error loading PDF: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() == null) return;

                if (!response.isSuccessful()) {
                    getActivity().runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Failed to load PDF",
                                Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                byte[] pdfBytes = response.body().bytes();

                getActivity().runOnUiThread(() -> {
                    try {
                        pdfView.fromBytes(pdfBytes)
                                .enableSwipe(true)
                                .swipeHorizontal(false)
                                .enableDoubletap(true)
                                .defaultPage(0)
                                .onLoad(nbPages -> {
                                    progressDialog.dismiss();
                                })
                                .onError(t -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(),
                                            "Error displaying PDF: " + t.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                })
                                .load();
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),
                                "Error displaying PDF: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
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

                    String examId = getArguments().getString("id");
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (examId != null && currentUser != null) {
                        List<Map<String, Object>> resultMap = new ArrayList<>();
                        for (Answer answer : results) {
                            Map<String, Object> answerMap = new HashMap<>();
                            answerMap.put("id", answer.getId());
                            answerMap.put("selectedAnswer", answer.getSelectedAnswer());
                            answerMap.put("correctAnswer", answer.getCorrectAnswer());
                            resultMap.add(answerMap);
                        }

                        Map<String, Object> examResult = new HashMap<>();
                        examResult.put("results", resultMap);
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
                                                showResults(resultMap, progressDialog);
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

    private void showResults(List<Map<String, Object>> resultMaps, ProgressDialog progressDialog) {
        if (!isAdded()) return; // Kiểm tra xem fragment đã được thêm vào hay chưa)
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadAndDisplayPdf(fileLink); // Thực hiện mở tài liệu khi quyền đã được cấp
            } else {
                Toast.makeText(getContext(), "Permission denied. Cannot open PDF.", Toast.LENGTH_SHORT).show();
            }
        }
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

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof StudentActivity) {
            ((StudentActivity) getActivity()).setBottomNavVisibility(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof StudentActivity) {
            ((StudentActivity) getActivity()).setBottomNavVisibility(true);
        }
    }
}