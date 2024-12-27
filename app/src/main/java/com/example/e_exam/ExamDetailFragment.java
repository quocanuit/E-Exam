package com.example.e_exam;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExamDetailFragment extends Fragment {
    private static final String ARG_CLASS_NAME = "className";
    private static final String ARG_NAME = "name";
    private static final String ARG_DUE_DATE = "dueDate";
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 1;

    private WebView pdfWebView;

    public static ExamDetailFragment newInstance(String className, String name, long dueDate, String fileUri) {
        ExamDetailFragment fragment = new ExamDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CLASS_NAME, className);
        args.putString(ARG_NAME, name);
        args.putLong(ARG_DUE_DATE, dueDate);
        args.putString("fileUri", fileUri); // Thêm URI file
        fragment.setArguments(args);
        return fragment;
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_detail, container, false);

        if (getArguments() != null) {
            // Các thông tin khác
            String fileUri = getArguments().getString("fileUri");

            WebView pdfWebView = view.findViewById(R.id.pdfWebView);
            WebSettings webSettings = pdfWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            pdfWebView.setWebViewClient(new WebViewClient());

            if (fileUri != null) {
                pdfWebView.loadUrl("https://docs.google.com/viewer?url=" + fileUri);
            }
        }
        return view;
    }

    private void openDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_DOCUMENT && resultCode == getActivity().RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                pdfWebView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + uri.toString());
            }
        }
    }
}
