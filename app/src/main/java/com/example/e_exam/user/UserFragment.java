package com.example.e_exam.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e_exam.R;

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

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<UserInfor> inforList;
    private TextView tvName, tvID_Position;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        recyclerView = view.findViewById(R.id.rv_Information);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        inforList = new ArrayList<>();
        inforList.add(new UserInfor("22520216@gm.uit.edu.vn", "07/01/2004", "Computer networks and data communications ", "Quang Nam", "MMTT2022.1"));

        userAdapter = new UserAdapter(inforList, getContext());
        recyclerView.setAdapter(userAdapter);

        tvName = view.findViewById(R.id.tv_Name);
        tvID_Position = view.findViewById(R.id.tv_ID_Pos);
        ivAvatar = view.findViewById(R.id.iv_Avatar);

        tvName.setText("Nguyễn Hữu Đạt");
        String position = isTeacher ? "Teacher" : "Student";
        tvID_Position.setText("22520216 | " + position);
        //tvID_Position.setText(id +" | " + position);

        if(position == "Teacher") {
            ivAvatar.setImageResource(R.drawable.teacher);
        }
        else
            ivAvatar.setImageResource(R.drawable.student);

        return view;
    }

}