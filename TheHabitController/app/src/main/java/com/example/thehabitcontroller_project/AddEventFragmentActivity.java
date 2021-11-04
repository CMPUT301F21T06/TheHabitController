package com.example.thehabitcontroller_project;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
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
public class AddEventFragmentActivity extends Fragment{

    private EditText title;
    private EditText comment;
    private TextView date;
    private Button setDateButton;
    private Button addButton;
    private Button cancelButton;
    private Location location;
    private Button addLocationButton;
    private String bitmapString;
    private Button addPhotoButton;

    public AddEventFragmentActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_habit_activity, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addButton = view.findViewById(R.id.addEventButton);
        cancelButton = view.findViewById(R.id.cancelAddEventButton);
        title = view.findViewById(R.id.event_title);
        comment = view.findViewById(R.id.event_comment);
        date = view.findViewById(R.id.dateEvent_editText);
        setDateButton = view.findViewById(R.id.btPickDateEvent);
        addLocationButton = view.findViewById(R.id.addEventLocationButton);
        addPhotoButton = view.findViewById(R.id.addEventPhotoButton);

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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle addEventBundle = new Bundle();

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
                String habit = title.getText().toString();
                String eventComment = comment.getText().toString();
                Location eventLocation = location; // temporary
                String bitmap = bitmapString; // temporary

                addEventBundle.putParcelable("addEvent", new Event(habit, eventComment, eventLocation, bitmap, inputDate));
                Navigation.findNavController(view).navigate(
                        R.id.action_addEventActivity_to_events,
                        addEventBundle
                );
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

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