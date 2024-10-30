package com.example.e_exam.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_exam.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<UserInfor> myList;
    private Context context;

    public UserAdapter(ArrayList<UserInfor> myList, Context context) {
        this.myList = myList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.listview_infor_student, parent, false);
        context = parent.getContext();
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserInfor userInfor = myList.get(position);

        holder.tvEmail.setText(userInfor.getEmail());
        holder.tvBirthday.setText(userInfor.getBirthday());
        holder.tvMajor.setText(userInfor.getSpecialized());
        holder.tvHometown.setText(userInfor.getHometown());
        holder.tvClass.setText(userInfor.getClass_activity());
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail, tvBirthday, tvMajor, tvHometown, tvClass;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEmail = itemView.findViewById(R.id.tv_Email);
            tvBirthday = itemView.findViewById(R.id.tv_Birthday);
            tvMajor = itemView.findViewById(R.id.tv_Major);
            tvHometown = itemView.findViewById(R.id.tv_Hometown);
            tvClass = itemView.findViewById(R.id.tv_Class);
        }
    }
}
