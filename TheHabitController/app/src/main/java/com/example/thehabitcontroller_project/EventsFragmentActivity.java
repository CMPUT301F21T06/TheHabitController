package com.example.thehabitcontroller_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Fragment} subclass to show the current {@link Event}s of the user
 * This class links with {@link FirebaseFirestore} in order to store, pull and update its data
 *
 * @author Tyler
 * @version 1.0.0
 */
public class EventsFragmentActivity extends Fragment {
    private List<Event> eventList;
    private ArrayAdapter<Event> eventArrayAdapter;
    private ListView eventListView;
    private FirebaseFirestore db;
    private String currentUser = "currentUser";
    final String DBTAG = "FireStore Call";

    public EventsFragmentActivity() {
        // Required empty public constructor
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
     * Override for extending the {@link Fragment} class that inflates
     * the fragment layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    /**
     * Override for extending the {@link Fragment} class for handling
     * building the structures after the view is created
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();
        eventArrayAdapter = new eventArrayAdapter(view.getContext(), eventList);
        eventListView = view.findViewById(R.id.event_list);
        eventListView.setAdapter(eventArrayAdapter);

        // initialize the event list from the FireStore database
        initializeEventList(view);

        // sets the listener for the button to add Events to the list
        final FloatingActionButton fab = view.findViewById(R.id.eventFloatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_events_to_addEventActivity);
            }
        });
    }

    /**
     * A helper method for setting up initializing our list of {@link Event} objects
     * This method will look at our {@link FirebaseFirestore} database and build our
     * list from the entries that exist in that collection.
     * @param view the current {@link View} we are in
     */
    private void initializeEventList(View view) {
        // initializes the Collection reference to the user's collection
        final DocumentReference userDr = db.collection("users").document(currentUser);
        final CollectionReference usersCr = userDr.collection("Events");

        usersCr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // start with an empty list then add all events to the list
                eventList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    eventList.add(doc.toObject(Event.class));
                }
                eventArrayAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if Firestore call fails it stores a log in DBTAG
                Log.d(DBTAG, "Was not able to get the data from Firestore to populate initial Event list");
            }
        });

        // if changes occur in the FIreStore db it will clear the list and re-add the events
        usersCr.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                eventList.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    eventList.add(doc.toObject(Event.class));
                }
                eventArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    public void addEvent(Event event) {
        // initializes the Collection reference to the user's collection
        final DocumentReference userDr = db.collection("users").document(currentUser);
        final CollectionReference usersCr = userDr.collection("Events");

        // adds the event to the list then renders changes
        eventArrayAdapter.add(event);
        eventArrayAdapter.notifyDataSetChanged();
        // adds event to the database
        usersCr.add(event);
    }
}