package com.example.e_exam;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_admin_class, container, false);

        // Khởi tạo Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Classes");

        // Khởi tạo danh sách lớp học
        classList = new ArrayList<>();

        // Khởi tạo RecyclerView và ánh xạ đúng ID từ layout
        recyclerView = view.findViewById(R.id.recyclerView_classes);
        if (recyclerView == null) {
            Log.e("FragmentAdminClass", "RecyclerView is null!");
        } else {
            // Thiết lập LayoutManager cho RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }

          // Khởi tạo Adapter và gắn nó vào RecyclerView
        classAdapter = new ClassAdapter(classList);
        if (recyclerView != null) {
            recyclerView.setAdapter(classAdapter);
        }

        // Xử lý sự kiện nhấn nút "Tạo lớp học"
        createClassButton = view.findViewById(R.id.btn_create_class);
        createClassButton.setOnClickListener(v -> showCreateClassDialog());

        // Xử lý sự kiện nhấn giữ lâu vào item lớp học
        classAdapter.setOnItemLongClickListener(this::showDeleteClassDialog);

        // Tải dữ liệu lớp học từ Firebase
        loadClasses();

        return view; // Trả về view đã được inflate
    }

    private void loadClasses() {
        // Lấy dữ liệu từ Firebase Realtime Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                classList.clear(); // Xóa danh sách cũ trước khi thêm mới

                // Duyệt qua các phần tử con trong "Classes"
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy giá trị của "className" từ mỗi phần tử
                    String className = snapshot.child("className").getValue(String.class);

                    if (className != null) {
                        // Thêm tên lớp học vào danh sách
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
        // Hiển thị hộp thoại tạo lớp học mới
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_class, null);

        EditText editClassName = dialogView.findViewById(R.id.edit_class_name);
        EditText editTeacherName = dialogView.findViewById(R.id.edit_teacher_name);
        EditText editTeacherId = dialogView.findViewById(R.id.edit_teacher_id); // EditText cho mã giảng viên
        Button btnCreate = dialogView.findViewById(R.id.btn_create);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnCreate.setOnClickListener(v -> {
            String className = editClassName.getText().toString().trim();
            String teacherName = editTeacherName.getText().toString().trim();
            String teacherID = editTeacherId.getText().toString().trim(); // Lấy mã giảng viên

            if (!className.isEmpty() && !teacherName.isEmpty() && !teacherID.isEmpty()) {
                Class newClass = new Class(className, teacherName, teacherID);
                databaseReference.push().setValue(newClass)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firebase", "Dữ liệu được đẩy lên thành công");
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firebase", "Lỗi khi đẩy dữ liệu: " + e.getMessage());
                            editClassName.setError("Không thể lưu lớp học vào database!");
                        });
            } else {
                if (className.isEmpty()) {
                    editClassName.setError("Tên lớp không được để trống!");
                }
                if (teacherName.isEmpty()) {
                    editTeacherName.setError("Tên giảng viên không được để trống!");
                }
                if (teacherID.isEmpty()) {
                    editTeacherId.setError("Mã giảng viên không được để trống!");
                }
            }
        });

        dialog.show();
    }

    private void showDeleteClassDialog(String className) {
        // Hiển thị hộp thoại xác nhận xóa lớp học
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xóa Lớp Học");
        builder.setMessage("Bạn có chắc chắn muốn xóa lớp học " + className + " không?");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            // Tìm và xóa lớp học khỏi Firebase Realtime Database
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Duyệt qua các phần tử con trong "Classes"
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // 1. Kiểm tra nhánh có trường "className" trùng với className
                        if (snapshot.hasChild("className")) {
                            String classNameInNode = snapshot.child("className").getValue(String.class);
                            if (classNameInNode != null && classNameInNode.equals(className)) {
                                snapshot.getRef().removeValue(); // Xóa nhánh có className trùng với className
                            }
                        }

                        // 2. Kiểm tra nhánh có tên trùng với className (tên nhánh cấp 1 dưới "Classes")
                        String key = snapshot.getKey();
                        if (key != null && key.equals(className)) {
                            // Xóa nhánh có tên nhánh trùng với className
                            snapshot.getRef().removeValue();

                            // 3. Xóa các nhánh con trực tiếp dưới nhánh có tên className
                            DataSnapshot studentsSnapshot = snapshot.child("students");
                            for (DataSnapshot studentSnapshot : studentsSnapshot.getChildren()) {
                                // Xóa từng sinh viên con dưới "students"
                                studentSnapshot.getRef().removeValue();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý lỗi nếu có sự cố khi xóa dữ liệu
                    Log.e("Firebase", "Lỗi khi xóa dữ liệu: " + databaseError.getMessage());
                }
            });
        });


        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
