package com.example.e_exam;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExamCreateFragment extends Fragment {

    private EditText examNameInput;
    private Spinner classPicker;
    private TextView deadlinePicker;
    private Button createExamButton;
    private String selectedClass;
    private Calendar selectedDeadline;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_create, container, false);

        examNameInput = view.findViewById(R.id.examNameInput);
        classPicker = view.findViewById(R.id.classPicker);
        deadlinePicker = view.findViewById(R.id.deadlinePicker);
        createExamButton = view.findViewById(R.id.createExamButton);

        // Set up the class picker with mock class names
        setupClassPicker();

        // Set up the deadline picker
        setupDeadlinePicker();

        // Button listener for exam creation
        createExamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String examName = examNameInput.getText().toString();
                if (examName.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter an exam name", Toast.LENGTH_SHORT).show();
                } else if (selectedDeadline == null) {
                    Toast.makeText(getContext(), "Please set a deadline", Toast.LENGTH_SHORT).show();
                } else {
                    // Simulating exam creation with mock data
                    String deadlineString = String.format("%02d-%02d-%d %02d:%02d",
                            selectedDeadline.get(Calendar.DAY_OF_MONTH),
                            selectedDeadline.get(Calendar.MONTH) + 1,
                            selectedDeadline.get(Calendar.YEAR),
                            selectedDeadline.get(Calendar.HOUR_OF_DAY),
                            selectedDeadline.get(Calendar.MINUTE));

                    Toast.makeText(getContext(), "Exam Created: " + examName
                                    + "\nClass: " + selectedClass
                                    + "\nDeadline: " + deadlineString,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    private void setupClassPicker() {
        // Mock list of class names
        List<String> classList = new ArrayList<>();
        classList.add("NT531");
        classList.add("NT533");
        classList.add("NT131");
        classList.add("NT118");

        // Create an ArrayAdapter for the spinner
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, classList);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classPicker.setAdapter(classAdapter);

        classPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClass = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupDeadlinePicker() {
        deadlinePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar currentDate = Calendar.getInstance();
                selectedDeadline = Calendar.getInstance();

                // Show Date Picker
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDeadline.set(year, month, dayOfMonth);

                        // Show Time Picker after setting the date
                        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectedDeadline.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDeadline.set(Calendar.MINUTE, minute);

                                // Display the selected deadline
                                deadlinePicker.setText(String.format("%d-%02d-%02d %02d:%02d", year, month + 1, dayOfMonth, hourOfDay, minute));
                            }
                        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
                    }
                }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
}
