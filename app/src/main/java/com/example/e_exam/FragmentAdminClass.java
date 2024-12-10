package com.example.e_exam;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FragmentAdminClass extends Fragment {
    private ArrayList<String> classList; // Danh sách tên lớp học
    private RecyclerView recyclerView; // RecyclerView để hiển thị danh sách lớp học
    private ClassAdapter classAdapter; // Adapter cho RecyclerView
    private Button createClassButton;
    private DatabaseReference databaseReference; // Tham chiếu tới Firebase Realtime Database

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_class, container, false);

        // Khởi tạo Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Classes");

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
        loadClasses();
        return view;
    }

    private void loadClasses() {
        // Lấy dữ liệu từ Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                classList.clear();  // Xóa danh sách cũ trước khi thêm mới

                // Duyệt qua các phần tử con trong "Classes"
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy giá trị của "className" và "teacherName" từ mỗi phần tử
                    String className = snapshot.child("className").getValue(String.class);
                    String teacherName = snapshot.child("teacherName").getValue(String.class);

                    if (className != null && teacherName != null) {
                        // Thêm tên lớp học và tên giảng viên vào danh sách
                        classList.add("Class: " + className + "\nTeacher: " + teacherName);


                    }
                }


                // Cập nhật lại Adapter với dữ liệu mới
                classAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hiển thị lỗi nếu có sự cố khi truy vấn dữ liệu
                Log.e("Firebase", "Lỗi khi lấy dữ liệu: " + databaseError.getMessage());
            }
        });
    }

    private void showCreateClassDialog() {
        // Tạo AlertDialog để nhập tên lớp học và tên giảng viên
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_class, null);

        EditText editClassName = dialogView.findViewById(R.id.edit_class_name);
        EditText editTeacherName = dialogView.findViewById(R.id.edit_teacher_name);  // Thêm EditText cho tên giảng viên
        Button btnCreate = dialogView.findViewById(R.id.btn_create);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnCreate.setOnClickListener(v -> {
            String className = editClassName.getText().toString().trim();
            String teacherName = editTeacherName.getText().toString().trim();  // Lấy tên giảng viên

            if (!className.isEmpty() && !teacherName.isEmpty()) {
                // Tạo đối tượng Class với cả tên lớp và tên giảng viên
                Class newClass = new Class(className, teacherName);

                // Thêm đối tượng Class vào danh sách cục bộ
                classList.add(String.valueOf(newClass));  // Thêm đối tượng mới vào danh sách
                classAdapter.notifyItemInserted(classList.size() - 1); // Thông báo rằng một mục đã được chèn vào cuối

                // Đẩy đối tượng Class lên Firebase Realtime Database
                databaseReference.push().setValue(newClass)
                        .addOnSuccessListener(aVoid -> {
                            // Đã đẩy dữ liệu thành công
                            Log.d("Firebase", "Dữ liệu được đẩy lên thành công");
                            dialog.dismiss(); // Đóng hộp thoại sau khi tạo lớp học thành công
                        })
                        .addOnFailureListener(e -> {
                            // In ra thông tin chi tiết lỗi khi đẩy dữ liệu thất bại
                            Log.e("Firebase", "Lỗi khi đẩy dữ liệu: " + e.getMessage());
                            e.printStackTrace();  // In stack trace của lỗi

                            // Hiển thị thông báo lỗi cho người dùng
                            editClassName.setError("Không thể lưu lớp học vào database!");
                        });
            } else {
                // Hiển thị thông báo lỗi khi tên lớp học hoặc tên giảng viên bị trống
                if (className.isEmpty()) {
                    editClassName.setError("Tên lớp không được để trống!");
                }
                if (teacherName.isEmpty()) {
                    editTeacherName.setError("Tên giảng viên không được để trống!");
                }
            }
        });

        // Kiểm tra xem hộp thoại có được hiển thị
        dialog.show();
    }
}
