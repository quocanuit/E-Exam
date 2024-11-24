package com.example.e_exam;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TeacherClassFragment extends Fragment {

    private ArrayList<String> classList; // Danh sách tên lớp học
    private RecyclerView recyclerView; // RecyclerView để hiển thị danh sách lớp học
    private ClassAdapter classAdapter; // Adapter cho RecyclerView
    private Button createClassButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_class, container, false);

        // Khởi tạo danh sách lớp học
        classList = new ArrayList<>();

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView_classes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        classAdapter = new ClassAdapter(classList);
        recyclerView.setAdapter(classAdapter);

        // Xử lý sự kiện nhấn nút "Tạo lớp học"
        createClassButton = view.findViewById(R.id.btn_create_class);
        createClassButton.setOnClickListener(v -> showCreateClassDialog());

        return view;
    }

    private void showCreateClassDialog() {
        // Tạo AlertDialog để nhập tên lớp học
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_class, null);

        EditText editClassName = dialogView.findViewById(R.id.edit_class_name);
        Button btnCreate = dialogView.findViewById(R.id.btn_create);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnCreate.setOnClickListener(v -> {
            String className = editClassName.getText().toString().trim();
            if (!className.isEmpty()) {
                classList.add(className);
                classAdapter.notifyItemInserted(classList.size() - 1); // Thông báo rằng một mục đã được chèn vào cuối
                dialog.dismiss(); // Đóng hộp thoại sau khi tạo
            } else {
                editClassName.setError("Tên lớp không được để trống!");
            }
        });

        dialog.show();
    }
}
