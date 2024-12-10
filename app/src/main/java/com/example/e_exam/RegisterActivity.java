package com.example.e_exam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.e_exam.databinding.ActivityRegisterBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo FirebaseApp trước khi sử dụng FirebaseAuth
        FirebaseApp.initializeApp(this);

        // Gán đối tượng binding với layout
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Xử lý sự kiện bấm nút Register
        binding.registerButton.setOnClickListener(v -> {
            registerUser();
        });

        // Xử lý sự kiện bấm vào "Already have an account? Login"
        binding.redirectToLogin.setOnClickListener(v -> {
            // Điều hướng người dùng quay lại màn hình đăng nhập
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Hàm đăng ký người dùng
    private void registerUser() {
        String email = binding.registerEmail.getText().toString().trim();
        String password = binding.registerPassword.getText().toString().trim();
        String confirmPassword = binding.registerConfirmPassword.getText().toString().trim();

        // Kiểm tra xem các trường có rỗng không
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu và xác nhận mật khẩu có khớp không
        if (!password.equals(confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đăng ký người dùng với Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng ký thành công
                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        // Điều hướng về màn hình đăng nhập (hoặc màn hình chính)
                        finish(); // Quay lại màn hình trước (MainActivity)
                    } else {
                        // Xảy ra lỗi
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Giải phóng đối tượng binding khi không còn sử dụng
        binding = null;
    }
}
