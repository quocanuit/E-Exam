package com.example.e_exam;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPass extends AppCompatActivity {

    private EditText forgotPasswordEmail;
    private Button forgotPasswordButton;
    private TextView redirectToLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_forgetpass);

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Lấy các thành phần trong layout
        forgotPasswordEmail = findViewById(R.id.forgotPasswordEmail);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        redirectToLogin = findViewById(R.id.redirectToLogin);

        // Xử lý sự kiện nhấn nút Gửi yêu cầu
        forgotPasswordButton.setOnClickListener(v -> {
            String email = forgotPasswordEmail.getText().toString().trim();

            // Kiểm tra xem email đã được nhập chưa
            if (email.isEmpty()) {
                Toast.makeText(ForgetPass.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi yêu cầu đặt lại mật khẩu
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgetPass.this, "Đã gửi liên kết đặt lại mật khẩu. Vui lòng kiểm tra email.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ForgetPass.this, "Có lỗi xảy ra. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Xử lý sự kiện quay lại trang đăng nhập
        redirectToLogin.setOnClickListener(v -> {
            finish(); // Quay lại màn hình đăng nhập (hoặc có thể gọi một Activity khác nếu cần)
        });
    }
}
