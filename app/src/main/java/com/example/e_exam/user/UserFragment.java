package com.example.e_exam.user;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e_exam.R;

import java.lang.annotation.Target;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NAME = "name";
    private static final String ARG_ID = "id";
    private static final String ARG_POSITION = "position";
    private static final String ARG_AVATAR = "avatar";

    private ArrayList<UserInformation> userList;
    private TextView tvName, tvID, tvEmail, tvBirthday, tvClass, tvHometown;
    private ImageButton btnNotification, btnSetting;
    private ImageView ivAvatar;

    // TODO: Rename and change types of parameters
    private String name;
    private String id;
    private boolean isTeacher;
    private int avatar;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name User's name.
     * @param id User's ID.
     * @param isTeacher Boolean to indicate if the user is a teacher.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String name, String id, boolean isTeacher, int avatar) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_ID, id);
        args.putBoolean(ARG_POSITION, isTeacher);
        args.putInt(ARG_AVATAR, avatar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
            id = getArguments().getString(ARG_ID);
            isTeacher = getArguments().getBoolean(ARG_POSITION);
            avatar = getArguments().getInt(ARG_AVATAR);
        }
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            String id = bundle.getString("id");
            String fullName = bundle.getString("fullName");
            String classAc = bundle.getString("class");

            // Handle the result here
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        tvName = view.findViewById(R.id.tv_Name);
        tvID = view.findViewById(R.id.tv_ID);
        tvEmail = view.findViewById(R.id.tv_Email);
        tvBirthday = view.findViewById(R.id.tv_Birthday);
        tvClass = view.findViewById(R.id.tv_Class);
        tvHometown = view.findViewById(R.id.tv_Hometown);
        btnNotification = view.findViewById(R.id.btn_Notification);
        btnSetting = view.findViewById(R.id.btn_Setting);

        ivAvatar = view.findViewById(R.id.iv_Avatar);

        userList = new ArrayList<>();
        userList.add(new UserInformation("Nguyễn Hữu Đạt","22520216", "22520216@gm.uit.edu.vn", "07/01/2004", "MMT&TT2022.1", "QUANG NAM", false));


        UserInformation user = userList.get(0);

        tvName.setText(user.getName());
        tvID.setText(user.getId());
        tvEmail.setText(user.getEmail());
        tvBirthday.setText(user.getBirthday());
        tvClass.setText(user.getClass_activity());
        tvHometown.setText(user.getHometown());

        Boolean position = user.isTeacher();
        if(position) {
            ivAvatar.setImageResource(R.drawable.teacher);
        }
        else
            ivAvatar.setImageResource(R.drawable.student);

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, changePasswordFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, changePasswordFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


        return view;
    }
}