package com.example.thehabitcontroller_project.Event;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.thehabitcontroller_project.Helper.DatePicker;
import com.example.thehabitcontroller_project.R;

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
        // set the current page with that info
        title.setText(selectedEvent.getTitle());
        comment.setText(selectedEvent.getComment());
        date.setText(selectedEvent.getFormattedDate());

        if (selectedEvent.getPhoto() != null) {
            eventPhotoView.setImageBitmap(selectedEvent.getPhoto());
            eventPhotoView.setVisibility(View.VISIBLE);
            photoString = selectedEvent.getPhotoString();
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
                getActivity().onBackPressed();
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
//                String photoString = "";
                // display photo if there is one
                if (eventPhotoView.getDrawable() != null) {
                    photo = ((BitmapDrawable) eventPhotoView.getDrawable()).getBitmap();

//                    ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
//                    photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
//                    photoString = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
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

                String habitTitle = selectedEvent.getHabitTitle();

                // put the new event in the bundle and the index of the event from the original list
                editEventBundle.putParcelable(
                        "editedEvent",
                        new Event(eventTitle, eventComment, inputDate, location, currentPhotoPath, habitTitle)
                );
                editEventBundle.putInt("index", currEventBundle.getInt("index"));

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
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);

        return image;
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
//        }
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
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        eventPhotoView.setImageBitmap(bitmap);
    }

    // Displays the photo
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == REQUEST_TAKE_PHOTO) {
            showPhoto();
        }
    }

}