package com.example.e_exam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private EditText emailInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Firebase Auth và Database Reference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Kiểm tra trạng thái đăng nhập
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful() && currentUser.isEmailVerified()) {
                    navigateToRoleActivity();
                } else if (currentUser != null && !currentUser.isEmailVerified()) {
                    Toast.makeText(this, "Please verify your email.", Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                }
            });
        }

        findViewById(R.id.btn_forget_password).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ForgetPass.class));
        });

        findViewById(R.id.btn_register).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });

        findViewById(R.id.loginButton).setOnClickListener(v -> {
            loginUser(); // Logic đăng nhập
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    user.reload().addOnCompleteListener(reloadTask -> {
                        if (reloadTask.isSuccessful() && user.isEmailVerified()) {
                            navigateToRoleActivity();
                        } else if (user != null && !user.isEmailVerified()) {
                            Toast.makeText(this, "Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        } else {
                            Toast.makeText(this, "Failed to refresh user state.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToRoleActivity() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        databaseReference.child(uid).get().addOnCompleteListener(databaseTask -> {
            if (databaseTask.isSuccessful() && databaseTask.getResult().exists()) {
                DataSnapshot userSnapshot = databaseTask.getResult();
                String role = userSnapshot.child("role").getValue(String.class);

                if ("Student".equals(role)) {
                    startActivity(new Intent(MainActivity.this, StudentActivity.class));
                } else if ("Teacher".equals(role)) {
                    startActivity(new Intent(MainActivity.this, TeacherActivity.class));
                } else if ("Admin".equals(role)) {
                    startActivity(new Intent(MainActivity.this, AdminActivityClass.class));
                } else {
                    Toast.makeText(this, "Invalid role assigned", Toast.LENGTH_SHORT).show();
                }
                finish();
            } else {
                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
