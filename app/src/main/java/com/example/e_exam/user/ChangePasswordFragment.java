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
    private View mView;
    private ProgressDialog progressDialog;
    private EditText edtOldPassword, edtNewPassword, edtConfirmNewPassword;
    private Button btnApply;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_change_password, container, false);

        initUI();

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Password Updated", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
                //onClickChangePassword();
            }
        });

        return mView;
    }

    private void initUI() {
        progressDialog = new ProgressDialog(getActivity());
        edtOldPassword = mView.findViewById(R.id.edt_Old_Password);
        edtNewPassword = mView.findViewById(R.id.edt_New_Password);
        edtConfirmNewPassword = mView.findViewById(R.id.edt_Confirm_New_Password);
        btnApply = mView.findViewById(R.id.btn_Apply);
    }

    private void onClickChangePassword(){
        String newPassword = edtNewPassword.getText().toString().trim();
        progressDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "User password updated.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}