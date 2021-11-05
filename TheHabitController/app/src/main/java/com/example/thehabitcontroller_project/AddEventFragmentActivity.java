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
 * A {@link Fragment} subclass to allow adding of {@link Event} objects to the event list
 * @author Tyler
 * @version 1.0.0
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

    /**
     * Override for extending the {@link Fragment} class that inflates the fragment layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_event_activity, container, false);
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
     * for adding events
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize all UI references used
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

        // set the add button listener
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start a new bundle for transferring the info back to habit list activity
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

                // collects info entered by user
                String event = title.getText().toString();
                String eventComment = comment.getText().toString();
                String eventLocation = "location";
//                Location eventLocation = new Location("Home"); // need to implement
                String bitmap = "bitmapString"; // need to implement

                // add the new event to the bundle
                addEventBundle.putParcelable("addEvent", new Event(event, eventComment, eventLocation, bitmap, inputDate));

                // navigate to the event list view
                Navigation.findNavController(view).navigate(
                        R.id.action_addEventActivity_to_events,
                        addEventBundle
                );
            }
        });

        // set listener for canceling to return to previous page
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