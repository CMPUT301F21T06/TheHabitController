package com.example.thehabitcontroller_project.Event;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.thehabitcontroller_project.Habit.Habit;
import com.example.thehabitcontroller_project.Helper.DatePicker;
import com.example.thehabitcontroller_project.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A {@link Fragment} subclass to allow editing of {@link Event}s of the user
 *
 * @author Tyler
 * @version 1.0.0
 */
public class EditEventFragmentActivity extends Fragment {

    private EditText title;
    private EditText comment;
    private TextView date;
    private Bitmap photo;
    private String photoString;
    private ImageView eventPhotoView;

    private Button setDateButton;
    private Button setLocationButton;
    private Button setPhotoButton;
    private Button saveButton;
    private Button cancelButton;
    private Button deleteButton;

    static final int REQUEST_TAKE_PHOTO = 1;
    private String currentPhotoPath;
    private String habitTitle;
    private Habit habit;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    final long ONE_MEGABYTE = 1024 * 1024;

    public EditEventFragmentActivity() {
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
        return inflater.inflate(R.layout.fragment_edit_event_activity, container, false);
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
     * for editing events
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize all UI references to be used later
        saveButton = view.findViewById(R.id.saveEventButton);
        cancelButton = view.findViewById(R.id.cancelEditEventButton);
        deleteButton = view.findViewById(R.id.deleteEventButton);
        title = view.findViewById(R.id.event_title);
        comment = view.findViewById(R.id.event_comment);
        date = view.findViewById(R.id.dateEvent_editText);
        eventPhotoView = view.findViewById(R.id.eventPhotoView);
        setDateButton = view.findViewById(R.id.btPickDateEvent);
        setLocationButton = view.findViewById(R.id.addEventLocationButton);
        setPhotoButton = view.findViewById(R.id.addEventPhotoButton);

        // get the bundle from the event fragment activity that has the selected event info
        Bundle currEventBundle = getArguments();
        Event selectedEvent = currEventBundle.getParcelable("Event");
        habit = currEventBundle.getParcelable("DailyHabit");

        // set the current page with that info
        title.setText(selectedEvent.getTitle());
        comment.setText(selectedEvent.getComment());
        date.setText(selectedEvent.getFormattedDate());

        // show photo if there is one
        if (selectedEvent.getPhotoString() != null) {
            StorageReference userStorageRef = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            final StorageReference imageRef = userStorageRef.child(habit.getTitle()).child(selectedEvent.getTitle()).child(selectedEvent.getPhotoString());
            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    eventPhotoView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                }
            });
//            eventPhotoView.setImageBitmap(selectedEvent.photoStringToBitmap());
//            eventPhotoView.setVisibility(View.VISIBLE);
//            photoString = selectedEvent.getPhotoString();
        }

        // set the onclicklistener for our set date button to pick a date
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initialize our DatePicker UI
                DatePicker date = new DatePicker();

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

        // set the listener to open the camera
        setPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Create the intent and open the camera
                takePicture();
            }
        });

        // set listener for the cancel button; just go back
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start our cancel event bundle
                Bundle cancelEventBundle = new Bundle();
                cancelEventBundle.putParcelable("DailyHabit", habit);

                // navigate back to event fragment activity
                Navigation.findNavController(view).navigate(
                        R.id.action_editEventFragmentActivity_to_events,
                        cancelEventBundle
                );
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    showPhoto();
                }
            }
        });

        // set listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start our edited event bundle
                Bundle editEventBundle = new Bundle();

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
                currentPhotoPath = "";
                String photoString = "";
                // display photo if there is one
                byte[] photoBytes = new byte[0];
                // display photo if there is one
                if (eventPhotoView.getDrawable() != null) {
                    photo = ((BitmapDrawable) eventPhotoView.getDrawable()).getBitmap();

                    ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
                    photoBytes = byteArrayOS.toByteArray();
                    photoString = "image.png";
                }

                // get the edited event's info
                String eventTitle = title.getText().toString();
                // if no event title is entered, return
                if (eventTitle.length() == 0) {
                    getActivity().onBackPressed();
                    return;
                }
                String eventComment = comment.getText().toString();

                String location = "";

                // add the photo to storage
                StorageReference userStorageRef = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                final StorageReference imageRef = userStorageRef.child(habit.getTitle()).child(eventTitle).child(photoString);
                imageRef.putBytes(photoBytes);

                String habitTitle = habit.getTitle();

                // put the new event in the bundle and the index of the event from the original list
                editEventBundle.putParcelable(
                        "editedEvent",
                        new Event(eventTitle, eventComment, inputDate, location, photoString, habitTitle)
                );
                editEventBundle.putInt("index", currEventBundle.getInt("index"));
                editEventBundle.putParcelable("DailyHabit", habit);

                // navigate to the event fragment activity
                Navigation.findNavController(view).navigate(
                    R.id.action_editEventFragmentActivity_to_events,
                    editEventBundle
                );
            }
        });

        // set listener for the delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start new bundle for deleting the event
                Bundle deleteEventBundle = new Bundle();

                // put the index of the event we want to delete
                deleteEventBundle.putInt("deleteIndex", currEventBundle.getInt("index"));
                deleteEventBundle.putParcelable("DailyHabit", habit);

                // navigate back to event fragment activity
                Navigation.findNavController(view).navigate(
                        R.id.action_editEventFragmentActivity_to_events,
                        deleteEventBundle
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

    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("temp",".jpg",storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        // Create file
        File f = new File(currentPhotoPath);
        MediaScannerConnection.scanFile(getContext(),
                new String[] {f.toString()},
                null, null);

        return image;
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(getActivity(),
                    "com.example.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            activityResultLauncher.launch(takePictureIntent);
        }
    }

    private void showPhoto() {
        // Get the dimensions of the View
        int targetW = eventPhotoView.getWidth();
        int targetH = eventPhotoView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        eventPhotoView.setImageBitmap(bitmap);
    }

}