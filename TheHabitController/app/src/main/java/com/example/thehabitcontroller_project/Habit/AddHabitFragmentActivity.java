package com.example.thehabitcontroller_project.Habit;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.thehabitcontroller_project.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A {@link Fragment} subclass to allow adding of {@link Habit} objects
 * to the list
 *
 * @author Steven
 * @version 1.0.0
 */
public class AddHabitFragmentActivity extends Fragment{

    private EditText title;
    private EditText reason;
    private TextView date;
    private Button setDateButton;
    private Button addButton;
    private Button cancelButton;
    private CheckBox isPublicCheckBox;
    private ChipGroup scheduleChipGroup;

    public AddHabitFragmentActivity() {
        // Required empty public constructor
    }

    /**
     * Override for extending the {@link Fragment} class that inflates
     * the fragment layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_habit_activity, container, false);
    }

    /**
     * Override for extending the {@link Fragment} class that just
     * calls its parent's implementation of onCreate()
     */
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    /**
     * Override for extending the {@link Fragment} class for handling
     * building the structures after the view is created and also setting functionality
     * for adding habits
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Add A New Habit");

        // initialize all UI references to be used later
        addButton = view.findViewById(R.id.addHabitButton);
        cancelButton = view.findViewById(R.id.cancelAddHabitButton);
        title = view.findViewById(R.id.habit_title);
        reason = view.findViewById(R.id.habit_reason);
        date = view.findViewById(R.id.date_editText);
        setDateButton = view.findViewById(R.id.btPickDate);
        isPublicCheckBox = view.findViewById(R.id.habitPublicCheckBox);
        scheduleChipGroup = view.findViewById(R.id.weekly_chip_group);

        // set the onclicklistener for our set date button to pick a date
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initialize our DatePicker UI
                com.example.thehabitcontroller_project.Helper.DatePicker date = new com.example.thehabitcontroller_project.Helper.DatePicker();

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

        // set the add button listener
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start a new bundle for transferring the info back to habit list activity
                Bundle addHabitBundle = new Bundle();

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
                // get the other info user put in
                String habitTitle = title.getText().toString();
                // if no habit is entered, return
                if (habitTitle.length() == 0) {
                    getActivity().onBackPressed();
                    return;
                }
                String habitReason = reason.getText().toString();
                boolean isPublic = isPublicCheckBox.isChecked();
                List<Boolean> schedule = new ArrayList<Boolean>(Arrays.asList(new Boolean[7]));
                for (int i = 0; i < scheduleChipGroup.getChildCount(); i++) {
                    Chip chip = (Chip) scheduleChipGroup.getChildAt(i);
                    schedule.set(i, chip.isChecked());
                }

                // add the new habit to the bundle
                addHabitBundle.putParcelable(
                    "addHabit",
                    new Habit(habitTitle, habitReason, inputDate, isPublic, schedule)
                );

                // navigate to the habit list view
                Navigation.findNavController(view).navigate(
                    R.id.action_addHabitActivity_to_habits,
                    addHabitBundle
                );
            }
        });

        // set listener for canceling, just go back to previous page
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    // attribute for the date listener for setting the date fragment on the page
    DatePickerDialog.OnDateSetListener onDate = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.getTime());
            date.setText(selectedDate);
        }
    };
}