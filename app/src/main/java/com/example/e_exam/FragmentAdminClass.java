package com.example.e_exam;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

    private void showCreateClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_class, null);

        EditText editClassName = dialogView.findViewById(R.id.edit_class_name);
        Spinner spinnerTeacherId = dialogView.findViewById(R.id.spinner_teacher_id);
        Button btnCreate = dialogView.findViewById(R.id.btn_create);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Load teacher IDs and names from Firebase
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
        ArrayList<String> teacherList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, teacherList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeacherId.setAdapter(adapter);

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teacherList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String role = snapshot.child("role").getValue(String.class);
                    if ("Teacher".equals(role)) {
                        String uid = snapshot.child("uid").getValue(String.class);
                        String fullName = snapshot.child("fullName").getValue(String.class);
                        if (uid != null && fullName != null) {
                            teacherList.add(uid + " - " + fullName);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Lỗi khi lấy dữ liệu: " + databaseError.getMessage());
            }
        });

        btnCreate.setOnClickListener(v -> {
            String className = editClassName.getText().toString().trim();
            String teacherInfo = (String) spinnerTeacherId.getSelectedItem();
            String teacherID = teacherInfo != null ? teacherInfo.split(" - ")[0] : "";
            String teacherName = teacherInfo != null ? teacherInfo.split(" - ")[1] : "";
            if (!className.isEmpty() && !teacherID.isEmpty()) {
                // Create a HashMap to store class data
                Class newClass = new Class(className, teacherID, teacherName);

                // Store the class directly under its name instead of using push()
                databaseReference.child(className).setValue(newClass)
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

                if (teacherID.isEmpty()) {
                    // Handle empty teacher ID
                    // You can show an error message or set a default value
                }
            }
        });

        dialog.show();
    }

    private void loadClasses() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                classList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the class name from the key
                    String className = snapshot.getKey();
                    if (className != null) {
                        classList.add(className);
                    }
                }

                classAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Lỗi khi lấy dữ liệu: " + databaseError.getMessage());
            }
        });
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
                            // Xóa nhánh con có tên trùng với className
                            snapshot.getRef().removeValue();
                        }
                    }

                    classList.remove(className); // Xóa lớp học khỏi danh sách lớp học
                    classAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Lỗi khi lấy dữ liệu: " + databaseError.getMessage());
                }
            });
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
