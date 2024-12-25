package com.example.e_exam.user;

import static com.example.e_exam.MainActivity.mAuth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import com.example.e_exam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {
    private static final int MIN_PASSWORD_LENGTH = 6;

    private View mView;
    private ProgressDialog progressDialog;
    private EditText edtOldPassword, edtNewPassword, edtConfirmNewPassword;
    private Button btnApply;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_change_password, container, false);
        initUI();
        setupListeners();
        return mView;
    }

    private void initUI() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Updating password...");
        progressDialog.setCancelable(false);

        edtOldPassword = mView.findViewById(R.id.edt_Old_Password);
        edtNewPassword = mView.findViewById(R.id.edt_New_Password);
        edtConfirmNewPassword = mView.findViewById(R.id.edt_Confirm_New_Password);
        btnApply = mView.findViewById(R.id.btn_Apply);
    }

    private void setupListeners() {
        btnApply.setOnClickListener(v -> validateAndChangePassword());
    }

    private void validateAndChangePassword() {
        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmNewPassword.getText().toString().trim();

        if (!validateInputs(oldPassword, newPassword, confirmPassword)) {
            return;
        }

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mAuth.signInWithEmailAndPassword(email, oldPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updatePassword(newPassword);
            }
            else {
                showAlertDialog("Thông báo", "Mật khẩu cũ không đúng");
            }
        });
    }

    private boolean validateInputs(String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlertDialog("Thông báo", "Vui lòng điền đầy đủ thông tin");
            return false;
        }

        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            showAlertDialog("Thông báo", "Mật khẩu mới phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlertDialog("Thông báo", "Mật khẩu mới không khớp");
            return false;
        }

        if (newPassword.equals(oldPassword)) {
            showAlertDialog("Thông báo", "Mật khẩu mới phải khác mật khẩu cũ");
            return false;
        }

        return true;
    }

    private void showAlertDialog(String title, String message) {
        if (getContext() == null) return;

        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void updatePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            showToast("User not logged in");
            return;
        }

        progressDialog.show();
        user.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        showAlertDialog("Notification", "Password updated successfully");
                        navigateBack();
                    } else {
                        showAlertDialog("Failed to update password: ",
                                (task.getException() != null ?
                                        task.getException().getMessage() : "Unknown error"));
                    }
                });
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateBack() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}