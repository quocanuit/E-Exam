package com.example.e_exam.user;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.e_exam.MainActivity;
import com.example.e_exam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;
import android.widget.Toast;

import java.io.IOException;


import de.hdodenhof.circleimageview.CircleImageView;


public class UserFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 10;

    private View view;
    private TextView tvName, tvID, tvEmail, tvBirthday, tvClass, tvHometown;
    private Button btnChangePassword, btnLogout;
    private CircleImageView btnAvatar;
    private DatabaseReference databaseReference;

    // Khai báo ActivityResultLauncher
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);
        setupActivityLaunchers();
        initUI();
        initListener();
        setupFirebase();
        loadUserProfile();
        return view;
    }

    private void setupFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        databaseReference.child(currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                updateUI(user);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),
                                "Error loading profile: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(User user) {
        tvName.setText(user.getFullName());
        tvID.setText(user.getUid());
        tvEmail.setText(user.getEmail());
        tvBirthday.setText(user.getBirthday());
//        tvClass.setText(user.getClass());

        // Hiển thị ảnh đại diện nếu có
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && firebaseUser.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(firebaseUser.getPhotoUrl())
                    .error(R.drawable.student)
                    .into(btnAvatar);
        }
    }

    private void setupActivityLaunchers() {
        // Khởi tạo permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                this::handlePermissionResult
        );

        // Khởi tạo gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleGalleryResult
        );
    }

    private void handlePermissionResult(Boolean isGranted) {
        if (isGranted) {
            openGallery();
        } else {
            Toast.makeText(getActivity(),
                    "Permission denied! Please allow access to continue.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void handleGalleryResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            try {
                Uri uri = result.getData().getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(), uri);
                btnAvatar.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(getActivity(),
                        "Failed to load image",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initUI() {
        tvName = view.findViewById(R.id.tv_Name);
        tvID = view.findViewById(R.id.tv_ID);
        tvEmail = view.findViewById(R.id.tv_Email);
        tvBirthday = view.findViewById(R.id.tv_Birthday);
        tvClass = view.findViewById(R.id.tv_Class);
        tvHometown = view.findViewById(R.id.tv_Hometown);
        btnChangePassword = view.findViewById(R.id.btn_ChangePassword);
        btnAvatar = view.findViewById(R.id.btn_Avatar);
        btnLogout = view.findViewById(R.id.btn_Logout);
    }

    private void initListener() {
        btnChangePassword.setOnClickListener(v -> navigateToChangePassword());
        btnAvatar.setOnClickListener(v -> checkAndRequestPermission());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent loginIntent = new Intent(getActivity(), MainActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        requireActivity().finish();
    }

    private void navigateToChangePassword() {
        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, changePasswordFragment)
                .addToBackStack(null)
                .commit();
    }

    private void checkAndRequestPermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else if (shouldShowRequestPermissionRationale(permission)) {
            showPermissionRationale(permission);
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    private void showPermissionRationale(String permission) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Permission Needed")
                .setMessage("This permission is needed to select a profile picture from your gallery")
                .setPositiveButton("OK", (dialog, which) ->
                        requestPermissionLauncher.launch(permission))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*");
        galleryLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
}