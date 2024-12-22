package com.example.e_exam.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.e_exam.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import javax.annotation.Nullable;

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

        updatePassword(newPassword);
    }

    private boolean validateInputs(String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Please fill in all fields");
            return false;
        }

        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            showToast("New password must be at least " + MIN_PASSWORD_LENGTH + " characters");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            showToast("New passwords don't match");
            return false;
        }

        if (newPassword.equals(oldPassword)) {
            showToast("New password must be different from old password");
            return false;
        }

        return true;
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
                        showToast("Password updated successfully");
                        navigateBack();
                    } else {
                        showToast("Failed to update password: " +
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