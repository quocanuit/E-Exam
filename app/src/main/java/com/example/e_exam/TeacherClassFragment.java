package com.example.e_exam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

public class TeacherClassFragment extends Fragment {

    private String teacherId; // Lưu teacherId từ TeacherActivity
    private RecyclerView recyclerView;
    private ClassAdapter classAdapter;
    private ArrayList<String> classList;
    private DatabaseReference databaseReference;

    // Constructor mặc định công khai
    public TeacherClassFragment() {
        // Bắt buộc để Android có thể tạo thể hiện của Fragment
    }

    // Phương thức newInstance để tạo instance của fragment với tham số teacherId
    public static TeacherClassFragment newInstance(String teacherId) {
        TeacherClassFragment fragment = new TeacherClassFragment();
        Bundle args = new Bundle();
        args.putString("teacherId", teacherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            teacherId = getArguments().getString("teacherId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_teacher_class, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewClasses); // Đảm bảo rằng ID này có trong layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        classList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Classes");

        // Truy vấn các lớp học mà giáo viên dạy
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                classList.clear();
                for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                    String teacherIdInClass = classSnapshot.child("teacherId").getValue(String.class);
                    if (teacherId.equals(teacherIdInClass)) {
                        String className = classSnapshot.child("className").getValue(String.class);
                        classList.add(className);
                    }
                }

                // Cập nhật RecyclerView với dữ liệu lớp học
                classAdapter = new ClassAdapter(classList);
                recyclerView.setAdapter(classAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
                Toast.makeText(getContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
