package com.example.e_exam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class AddExam extends AppCompatActivity {

    private ListView listViewExams;
    private ArrayList<String> examList;
    private ArrayAdapter<String> examAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.up_exam);

        listViewExams = findViewById(R.id.listview_assignments);
        Button btnAddExam = findViewById(R.id.btn_add_assignment);

        // Khởi tạo danh sách bài tập
        examList = new ArrayList<>();
        examAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, examList);
        listViewExams.setAdapter(examAdapter);

        // Thiết lập sự kiện khi nhấn nút "Thêm bài tập"
        btnAddExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddExamDialog();
            }
        });
    }

    // Hàm để hiển thị Dialog nhập bài tập
    private void showAddExamDialog() {
        // Tạo đối tượng LayoutInflater
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_assignment, null);

        // Lấy các EditText trong Dialog
        final EditText idAssignment = dialogView.findViewById(R.id.id_asignment);
        final EditText deadline = dialogView.findViewById(R.id.deadline);
        Button btnCreateAssignment = dialogView.findViewById(R.id.btn_create);

        // Tạo Dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);
        final Dialog dialog = dialogBuilder.create();

        // Xử lý sự kiện khi người dùng nhấn "Tạo bài tập"
        btnCreateAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String assignmentId = idAssignment.getText().toString();
                String assignmentDeadline = deadline.getText().toString();

                if (!assignmentId.isEmpty() && !assignmentDeadline.isEmpty()) {
                    // Thêm bài tập vào danh sách
                    examList.add("Mã bài tập: " + assignmentId + " - Ngày kết thúc: " + assignmentDeadline);
                    examAdapter.notifyDataSetChanged(); // Cập nhật ListView
                    dialog.dismiss(); // Đóng Dialog
                } else {
                    // Nếu các trường trống, hiển thị thông báo
                    idAssignment.setError("Vui lòng điền đầy đủ thông tin!");
                    deadline.setError("Vui lòng điền đầy đủ thông tin!");
                }
            }
        });

        dialog.show();
    }
}
