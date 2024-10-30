package com.example.e_exam;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.app.AlertDialog;
import com.example.e_exam.databinding.ActivityTeacherBinding;
import java.util.ArrayList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.e_exam.user.UserFragment;
import retrofit2.Retrofit;

public class TeacherActivity extends AppCompatActivity {

    ActivityTeacherBinding binding;
    ArrayList<String> classList; // Danh sách tên lớp học
    ClassAdapter classAdapter; // Adapter cho RecyclerView
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTeacherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Khởi tạo danh sách lớp học
        classList = new ArrayList<>();
        classAdapter = new ClassAdapter(classList);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(classAdapter);

        replaceFragment(new TeacherClassFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.classroom) {
                replaceFragment(new TeacherClassFragment());
            } else if (itemId == R.id.exam) {
                replaceFragment(new TeacherExamFragment());
            } else if (itemId == R.id.user) {
                replaceFragment(new UserFragment());
            }

            return true;
        });

        // Xử lý sự kiện nhấn nút "Tạo lớp học"
        Button createClassButton = findViewById(R.id.btn_create_class);
        createClassButton.setOnClickListener(v -> showCreateClassDialog());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void showCreateClassDialog() {
        // Tạo AlertDialog để nhập tên lớp học
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_class, null);

        EditText editClassName = dialogView.findViewById(R.id.edit_class_name);
        Button btnCreate = dialogView.findViewById(R.id.btn_create);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnCreate.setOnClickListener(v -> {
            String className = editClassName.getText().toString().trim();
            if (!className.isEmpty()) {
                classList.add(className);
                classAdapter.notifyItemInserted(classList.size() - 1);
                dialog.dismiss(); // Đóng hộp thoại sau khi tạo
            } else {
                editClassName.setError("Tên lớp không được để trống!");
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}
