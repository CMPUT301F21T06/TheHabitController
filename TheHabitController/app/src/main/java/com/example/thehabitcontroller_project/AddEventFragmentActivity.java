package com.example.thehabitcontroller_project;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.content.pm.PackageManager;

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
    private Bitmap photo;
    private Button addPhotoButton;
    private ImageView eventPhotoView;
    private Habit habit;

    private static final int requestCode = 1;

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
        eventPhotoView = view.findViewById(R.id.eventPhotoView);

        getHabitBundle(view);

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

                // set empty photo if no photo taken
                String photoString = "";
                if (eventPhotoView.getDrawable() != null) {
                    photo = ((BitmapDrawable) eventPhotoView.getDrawable()).getBitmap();
                    ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
                    photoString = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
                }

                // collects info entered by user
                String event = title.getText().toString();
                String eventComment = comment.getText().toString();
                String eventLocation = "location";
//                Location eventLocation = new Location("Home"); // need to implement

                // initialize database call
                FirebaseFirestore db =  FirebaseFirestore.getInstance();
                String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final DocumentReference userDr = db.collection("users").document(currentUser);
                final CollectionReference usersCr = userDr.collection("Habits");
                // set the habit's completed value by 1
                usersCr.document(habit.getTitle()).update(habit.getTimesFinishedString(), FieldValue.increment(1));

                // TODO: attach habit to the event

                // add the new event to the bundle
                addEventBundle.putParcelable("addEvent", new Event(event, eventComment, inputDate, eventLocation, "photoString"));

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

        // set the listener to open the camera
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Create the intent and open the camera
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (photoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(photoIntent, requestCode);
//                }
            }
        });
    }

    private void getHabitBundle(View view) {
        Bundle currBundle = getArguments();
        if (!currBundle.isEmpty()) {
            // see if we've added a Habit
            Habit currHabit = currBundle.getParcelable("Habit");
            if (currHabit != null) {
                this.habit = currHabit;
            }

            // TODO: Add functionality to this so habit is attached to event

            // clear args so we refresh for next commands
            getArguments().clear();
        }
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

    // method stores and displays the photo
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        // Match the pic id with requestCode
        if (reqCode == requestCode) {

            Bundle extras = data.getExtras();
            // BitMap stores the image
            Bitmap photoBitmap = (Bitmap) extras.get("data");

            // Display the image
            eventPhotoView.setImageBitmap(photoBitmap);
            eventPhotoView.setVisibility(View.VISIBLE);
        }
    }
}