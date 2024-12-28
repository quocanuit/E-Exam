package com.example.e_exam;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.activity.EdgeToEdge;
import com.example.e_exam.databinding.ActivityTeacherBinding;
import java.util.ArrayList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.e_exam.user.UserFragment;

public class TeacherActivity extends AppCompatActivity {

    ActivityTeacherBinding binding;
    private String teacherId; // Biến để lưu teacherId
    ArrayList<String> classList; // Danh sách tên lớp học
    ClassAdapter classAdapter; // Adapter cho RecyclerView
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTeacherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lấy teacherId từ Intent
        teacherId = getIntent().getStringExtra("Customuid");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Gọi fragment TeacherClassFragment và truyền teacherId vào
        replaceFragment(TeacherClassFragment.newInstance(teacherId));
        replaceFragment(TeacherExamFragment.newInstance(teacherId));// Truyền teacherId vào fragment

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.classroom) {
                replaceFragment(TeacherClassFragment.newInstance(teacherId)); // Truyền teacherId khi chọn lớp học
            } else if (itemId == R.id.exam) {
                replaceFragment(TeacherExamFragment.newInstance(teacherId));
            } else if (itemId == R.id.user) {
                replaceFragment(new UserFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}