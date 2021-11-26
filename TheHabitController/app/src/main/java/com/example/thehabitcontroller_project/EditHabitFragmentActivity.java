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
 * A {@link Fragment} subclass to allow editing of {@link Habit}s of the user
 *
 * @author Steven
 * @version 1.0.0
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
    private ChipGroup scheduleChipGroup;
    private Habit selectedHabit;


    public EditHabitFragmentActivity() {
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
        return inflater.inflate(R.layout.fragment_edit_habit_activity, container, false);
    }

    /**
     * Override for extending the {@link Fragment} class that just
     * calls its parent's implementation of onCreate()
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Override for extending the {@link Fragment} class for handling
     * building the structures after the view is created and also setting functionality
     * for editing habits
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize all UI references to be used later
        saveButton = view.findViewById(R.id.saveHabitButton);
        cancelButton = view.findViewById(R.id.cancelEditHabitButton);
        deleteButton = view.findViewById(R.id.deleteHabitButton);
        title = view.findViewById(R.id.habit_title);
        reason = view.findViewById(R.id.habit_reason);
        date = view.findViewById(R.id.date_editText);
        setDateButton = view.findViewById(R.id.btPickDate);
        // can't change date after Habit is created
        setDateButton.setVisibility(View.INVISIBLE);
        isPublicCheckBox = view.findViewById(R.id.habitPublicCheckBox);
        scheduleChipGroup = view.findViewById(R.id.weekly_chip_group);

        // get the bundle from the habit fragment activity that has the selected habit info
        Bundle currHabitBundle = getArguments();
        selectedHabit = currHabitBundle.getParcelable("Habit");
        // set the title specific to the Habit
        getActivity().setTitle("'" + selectedHabit.getTitle() + "' Habit");
        // set the current page with that info
        title.setText(selectedHabit.getTitle());
        reason.setText(selectedHabit.getReason());
        date.setText(selectedHabit.getFormattedDate());
        isPublicCheckBox.setChecked(selectedHabit.isPublic());
        List<Boolean> selectedHabitSchedule = selectedHabit.getSchedule();
        for (int i = 0; i < scheduleChipGroup.getChildCount() && i < selectedHabitSchedule.size(); i++) {
            Chip chip = (Chip) scheduleChipGroup.getChildAt(i);
            chip.setChecked(selectedHabitSchedule.get(i));
        }

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

        // set listener for the cancel button; just go back
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        // set listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start our edited habit bundle
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
                // get the edited habit's info
                String habitTitle = title.getText().toString();
                // if no habit title is entered, return
                if (habitTitle.length() == 0) {
                    getActivity().onBackPressed();
                    return;
                }
                String habitReason = reason.getText().toString();
                boolean isPublic = isPublicCheckBox.isChecked();
                // setup the habit's weekly schedule
                List<Boolean> schedule = new ArrayList<Boolean>(Arrays.asList(new Boolean[7]));
                for (int i = 0; i < scheduleChipGroup.getChildCount(); i++) {
                    Chip chip = (Chip) scheduleChipGroup.getChildAt(i);
                    schedule.set(i, chip.isChecked());
                }
                // get the habit's stats of completion
                int timesDone = selectedHabit.getTimesFinished();
                int totalTimesShown = selectedHabit.getTotalShownTimes();
                // put the new habit in the bundle and the index of the habit from the original list
                editHabitBundle.putParcelable(
                        "editedHabit",
                        new Habit(habitTitle, habitReason, inputDate, isPublic, schedule, timesDone, totalTimesShown)
                );
                editHabitBundle.putInt("index", currHabitBundle.getInt("index"));

                // navigate to the habit fragment activity
                Navigation.findNavController(view).navigate(
                    R.id.action_editHabitFragmentActivity_to_habits,
                    editHabitBundle
                );
            }
        });

        // set listener for the delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start new bundle for deleting the habit
                Bundle deleteHabitBundle = new Bundle();

                // put the index of the habit we want to delete
                deleteHabitBundle.putInt("deleteIndex", currHabitBundle.getInt("index"));

                // navigate back to habit fragment activity
                Navigation.findNavController(view).navigate(
                        R.id.action_editHabitFragmentActivity_to_habits,
                        deleteHabitBundle
                );
            }
        });
    }

    // attribute for the date listener for setting the date fragment on the page
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