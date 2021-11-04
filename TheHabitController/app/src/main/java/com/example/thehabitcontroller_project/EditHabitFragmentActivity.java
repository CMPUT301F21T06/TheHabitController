package com.example.thehabitcontroller_project;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class EditHabitFragmentActivity extends Fragment {

    private EditText title;
    private EditText reason;
    private TextView date;
    private CheckBox isPublicCheckBox;
    private Button setDateButton;
    private Button saveButton;
    private Button cancelButton;
    private Button deleteButton;


    public EditHabitFragmentActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_habit_activity, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saveButton = view.findViewById(R.id.saveHabitButton);
        cancelButton = view.findViewById(R.id.cancelEditHabitButton);
        deleteButton = view.findViewById(R.id.deleteHabitButton);
        title = view.findViewById(R.id.habit_title);
        reason = view.findViewById(R.id.habit_reason);
        date = view.findViewById(R.id.date_editText);
        setDateButton = view.findViewById(R.id.btPickDate);
        isPublicCheckBox = view.findViewById(R.id.habitPublicCheckBox);

        Bundle currHabitBundle = getArguments();
        Habit selectedHabit = currHabitBundle.getParcelable("Habit");
        title.setText(selectedHabit.getTitle());
        reason.setText(selectedHabit.getReason());
        date.setText(selectedHabit.getFormattedDate());
        isPublicCheckBox.setChecked(selectedHabit.isPublic());

        // set the onclicklistener for our set date button to pick a date
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initialize our DatePicker UI
                com.example.thehabitcontroller_project.DatePicker date = new com.example.thehabitcontroller_project.DatePicker();

                // Set Up Current Date Into dialog
                Calendar calender = Calendar.getInstance();
                Bundle args = new Bundle();
                args.putInt("year", calender.get(Calendar.YEAR));
                args.putInt("month", calender.get(Calendar.MONTH));
                args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
                date.setArguments(args);

                // Set Call back to capture selected date
                date.setCallBack(onDate);
                date.show(getParentFragmentManager(), "Date Picker");
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle editHabitBundle = new Bundle();

                // set date to be current date if no input was found
                Date inputDate = new Date();
                // if we have date input, do the try catch for parsing the date
                if (date.length() != 0) {
                    try {
                        inputDate = new SimpleDateFormat("EEEEE, MMMMM dd, yyyy").parse(date.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                String habitTitle = title.getText().toString();
                String habitReason = reason.getText().toString();
                boolean isPublic = isPublicCheckBox.isChecked();

                editHabitBundle.putParcelable("editedHabit", new Habit(habitTitle, habitReason, inputDate, isPublic));
                editHabitBundle.putInt("index", currHabitBundle.getInt("index"));
                Navigation.findNavController(view).navigate(
                    R.id.action_editHabitFragmentActivity_to_habits,
                    editHabitBundle
                );
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle deleteHabitBundle = new Bundle();
                deleteHabitBundle.putInt("deleteIndex", currHabitBundle.getInt("index"));
                Navigation.findNavController(view).navigate(
                        R.id.action_editHabitFragmentActivity_to_habits,
                        deleteHabitBundle
                );
            }
        });
    }

    DatePickerDialog.OnDateSetListener onDate = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.getTime());
            date.setText(selectedDate);
        }
    };
}