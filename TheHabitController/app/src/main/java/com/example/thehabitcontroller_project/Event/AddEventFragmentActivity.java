package com.example.thehabitcontroller_project.Event;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
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

import com.example.thehabitcontroller_project.Habit.Habit;
import com.example.thehabitcontroller_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
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
    private Bitmap photo;
    private Button addPhotoButton;
    private ImageView eventPhotoView;
    private Habit habit;

    static final int REQUEST_TAKE_PHOTO = 1;
    private String currentPhotoPath;
    private String habitTitle;
    private ActivityResultLauncher<Intent> activityResultLauncher;

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
                currentPhotoPath = "";
                String photoString = null;
                // display photo if there is one
                byte[] photoBytes = new byte[0];
                if (eventPhotoView.getDrawable() != null) {
                    photo = ((BitmapDrawable) eventPhotoView.getDrawable()).getBitmap();

                    ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
                    photoBytes = byteArrayOS.toByteArray();
                    photoString = "image.png";
                }

                // collects info entered by user
                String event = title.getText().toString();
                String eventComment = comment.getText().toString();

                String eventLocation = "location"; // location not implemented
                //Location eventLocation = new Location("Home");

                // initialize database call
                FirebaseFirestore db =  FirebaseFirestore.getInstance();
                String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final DocumentReference userDr = db.collection("users").document(currentUser);
                final CollectionReference usersCr = userDr.collection("Habits");
                // set the habit's completed value by 1
                usersCr.document(habit.getTitle()).update(habit.getTimesFinishedString(), FieldValue.increment(1));

                // add the photo to storage
                if (photoBytes.length > 0) {
                    StorageReference userStorageRef = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    final StorageReference imageRef = userStorageRef.child(habit.getTitle()).child(event).child(photoString);
                    imageRef.putBytes(photoBytes);
                }

                // attach habit to the event
                habitTitle = habit.getTitle();

                // add the new event to the bundle
                addEventBundle.putParcelable("addEvent", new Event(event, eventComment, inputDate, eventLocation, photoString, habitTitle));
                addEventBundle.putParcelable("DailyHabit", habit);

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
                // start our cancel event bundle
                Bundle cancelEventBundle = new Bundle();
                cancelEventBundle.putParcelable("DailyHabit", habit);

                // navigate back to event fragment activity
                Navigation.findNavController(view).navigate(
                        R.id.action_addEventActivity_to_events,
                        cancelEventBundle
                );
            }
        });

       activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
           @Override
           public void onActivityResult(ActivityResult result) {
               if (result.getResultCode() == RESULT_OK) {
                   showPhoto();
               }
           }
       });
        // set the listener to open the camera
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Create the intent and open the camera
                takePicture();
            }
        });
    }

    private void registerForActivityResult(ActivityResultContracts.TakePicture takePicture) {
    }

    // gets the current Habit from the bundle
    private void getHabitBundle(View view) {
        Bundle currBundle = getArguments();
        if (currBundle != null) {
            // see if we've added a Habit
            Habit currHabit = currBundle.getParcelable("DailyHabit");
            if (currHabit != null) {
                this.habit = currHabit;
            }
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

    // Creates the image file when taking a picture
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

    // Used to launch the camera and take a picture
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

    // Displays the photo in the imageview
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