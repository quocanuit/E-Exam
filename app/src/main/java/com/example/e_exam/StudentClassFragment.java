package com.example.e_exam;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentClassFragment extends Fragment implements ClassAdapterStudent.OnClassJoinListener {

    private SearchView timLop;
    private RecyclerView dsLop;
    private ClassAdapterStudent classAdapter;
    private ArrayList<String> classList;
    private DatabaseReference databaseReference;
    private String studentId;

    public StudentClassFragment(String studentId) {
        this.studentId = studentId; // Nhận studentId từ intent
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_class, container, false);

        timLop = view.findViewById(R.id.tim_lop);
        dsLop = view.findViewById(R.id.ds_lop);

        dsLop.setLayoutManager(new LinearLayoutManager(getContext()));
        classList = new ArrayList<>();
        classAdapter = new ClassAdapterStudent(classList, getContext(), this, studentId); // Truyền studentId vào
        dsLop.setAdapter(classAdapter);

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Classes");

        loadJoinedClasses();

        timLop.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchClasses(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    loadJoinedClasses();
                } else {
                    searchClasses(newText);
                }
                return false;
            }
        });

        return view;
    }

    private void loadJoinedClasses() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                classList.clear();
                for (DataSnapshot classSnapshot : snapshot.getChildren()) {
                    DataSnapshot studentsSnapshot = classSnapshot.child("students");
                    for (DataSnapshot studentSnapshot : studentsSnapshot.getChildren()) {
                        String studentIdInClass = studentSnapshot.child("studentId").getValue(String.class);
                        if (studentIdInClass != null && studentIdInClass.equals(studentId)) {
                            String className = classSnapshot.getKey();
                            if (className != null) {
                                classList.add(className);
                            }
                            break;
                        }
                    }
                }
                classAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchClasses(String query) {
        databaseReference.orderByChild("className").startAt(query).endAt(query + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        classList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String className = dataSnapshot.child("className").getValue(String.class);
                            if (className != null) {
                                classList.add(className);
                            }
                        }
                        classAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClassJoin(String className, String studentName) {
        Student student = new Student(studentId, studentName);
        databaseReference.child(className).child("students").child(studentId)
                .setValue(student)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã tham gia lớp thành công", Toast.LENGTH_SHORT).show();
                    loadJoinedClasses(); // Cập nhật danh sách lớp đã tham gia
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Tham gia lớp thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
