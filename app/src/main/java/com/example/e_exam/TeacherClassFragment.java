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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TeacherClassFragment extends Fragment {

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
                    // Lấy giá trị của "className" từ mỗi phần tử
                    String className = snapshot.child("className").getValue(String.class);

                    if (className != null) {
                        // Thêm className vào danh sách
                        classList.add(className);
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
        // Tạo AlertDialog để nhập tên lớp học và mã lớp
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_class, null);

        EditText editClassName = dialogView.findViewById(R.id.edit_class_name);
        EditText editClassId = dialogView.findViewById(R.id.edit_class_id);  // Thêm EditText cho mã lớp
        Button btnCreate = dialogView.findViewById(R.id.btn_create);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnCreate.setOnClickListener(v -> {
            String className = editClassName.getText().toString().trim();
            String classId = editClassId.getText().toString().trim();  // Lấy mã lớp

            if (!className.isEmpty() && !classId.isEmpty()) {
                // Tạo đối tượng Class với cả tên và mã lớp
                Class newClass = new Class(className, classId);

                // Thêm tên lớp vào danh sách cục bộ
                classList.add(className);
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
                // Hiển thị thông báo lỗi khi tên lớp học hoặc mã lớp bị trống
                if (className.isEmpty()) {
                    editClassName.setError("Tên lớp không được để trống!");
                }
                if (classId.isEmpty()) {
                    editClassId.setError("Mã lớp không được để trống!");
                }
            }
        });

        // Kiểm tra xem hộp thoại có được hiển thị
        dialog.show();
    }
}