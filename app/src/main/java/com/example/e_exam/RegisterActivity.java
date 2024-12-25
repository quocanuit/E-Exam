package com.example.e_exam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.e_exam.databinding.ActivityRegisterBinding;
import com.example.e_exam.user.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo FirebaseApp
        FirebaseApp.initializeApp(this);

        // Gán đối tượng binding với layout
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo FirebaseAuth và DatabaseReference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Xử lý sự kiện bấm nút Register
        binding.registerButton.setOnClickListener(v -> {
            registerUser();
        });

        // Xử lý sự kiện bấm vào "Already have an account? Login"
        binding.redirectToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Hàm đăng ký người dùng
    private void registerUser() {
        String email = binding.registerEmail.getText().toString().trim();
        String password = binding.registerPassword.getText().toString().trim();
        String confirmPassword = binding.registerConfirmPassword.getText().toString().trim();
        String fullName = binding.fullNameInput.getText().toString().trim();
        String birthday = binding.birthdayInput.getText().toString().trim();
        String uniclass = binding.uniClassInput.getText().toString().trim();

        // Lấy vai trò từ RadioGroup
        int selectedRoleId = binding.roleRadioGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedRoleButton = findViewById(selectedRoleId);
        String role = selectedRoleButton.getText().toString();

        // Kiểm tra xem các trường có rỗng không
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                role.isEmpty() || birthday.isEmpty() || fullName.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu và xác nhận mật khẩu có khớp không
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đăng ký người dùng với Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Lấy UID do Firebase tạo ra
                        String firebaseUID = mAuth.getCurrentUser().getUid();

                        // Tạo UID tùy chỉnh (role_Ten_ddmm)
                        String[] nameParts = fullName.split(" ");
                        String shortName = nameParts[nameParts.length - 1]; // Lấy tên cuối cùng
                        String shortBirthday = birthday.replace("/", "").substring(0, 4); // ddmm
                        String customUID = role.equalsIgnoreCase("teacher") ? "gv_" :
                                role.equalsIgnoreCase("student") ? "sv_" :
                                        role.equalsIgnoreCase("admin") ? "ad_" : "";
                        customUID += shortName + "_" + shortBirthday;

                        // Tạo đối tượng User
                        User user = new User(customUID, fullName, birthday, email, role, uniclass);

                        // Lưu thông tin người dùng vào Realtime Database
                        databaseReference.child(firebaseUID).setValue(user)
                                .addOnCompleteListener(databaseTask -> {
                                    if (databaseTask.isSuccessful()) {
                                        // Gửi email xác minh
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        if (firebaseUser != null) {
                                            firebaseUser.sendEmailVerification()
                                                    .addOnCompleteListener(emailTask -> {
                                                        if (emailTask.isSuccessful()) {
                                                            Toast.makeText(this, "Registration successful. Please verify your email.", Toast.LENGTH_LONG).show();
                                                            finish();
                                                        } else {
                                                            Toast.makeText(this, "Failed to send verification email: " + emailTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        }
                                    } else {
                                        Toast.makeText(this, "Failed to save user: " + databaseTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Giải phóng đối tượng binding
    }
}
