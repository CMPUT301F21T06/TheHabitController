package com.example.thehabitcontroller_project;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        FloatingActionButton fab = view.findViewById(R.id.eventFloatingActionButton);
        NavController navController = Navigation.findNavController(view);
        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();
        eventArrayAdapter = new EventArrayAdapter(view.getContext(), eventList);
        eventListView = view.findViewById(R.id.event_list);
        eventListView.setAdapter(eventArrayAdapter);

        // initialize the event list from the FireStore database
        initializeEventList(view);

        // sets the listener for the button to add Events to the list
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_events_to_addEventActivity);
            }
        });

        // set the listener for any user tapping on an item in the list of events
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle editEventBundle = new Bundle();
                Event e = eventArrayAdapter.getItem(i);
                editEventBundle.putParcelable("Event", e);
                editEventBundle.putInt("index", i);
                navController.navigate(R.id.action_events_to_editEventFragmentActivity, editEventBundle);
            }
        });
    }

    /**
     * A helper method for organizing when we return from adding or editing a {@link Event} in the list
     * This will go through all possible {@link Bundle} objects to see what to do if a user
     * has edited, added or deleted a {@link Event}
     */
    private void checkEventListChanges() {
        // first get the bundle from the arguments
        Bundle currBundle = getArguments();
        // if it's empty, then we do nothing
        if (currBundle != null) {
            // see if we've added an event
            Event addEvent = currBundle.getParcelable("addEvent");
            if (addEvent != null) {
                addEvent(addEvent);
            }

            // see if we have an event that was edited
            Event editedEvent = currBundle.getParcelable("editedEvent");
            if (editedEvent != null) {
                int index = currBundle.getInt("index");
                deleteEvent(index);
                insertEvent(editedEvent, index);
            }

            // see if we've deleted an event
            int deleteIndex = currBundle.getInt("deleteIndex", -1);
            if (deleteIndex != -1) {
                deleteEvent(deleteIndex);
            }

            // clear args so we refresh for next commands
            getArguments().clear();
        }
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
                checkEventListChanges();
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

    /**
     * Method for adding an event to our {@link FirebaseFirestore} database as well as our {@link Event}
     * object list that's shown on the current fragment.
     * @param event the {@link Event} to be added to the database and listview
     */
    public void addEvent(Event event) {
        // initializes the Collection reference to the user's collection
        final DocumentReference userDr = db.collection("users").document(currentUser);
        final CollectionReference usersCr = userDr.collection("Events");

        // adds the event to the list then renders changes
        eventArrayAdapter.add(event);
        eventArrayAdapter.notifyDataSetChanged();
        // adds event to the database
        usersCr.document(event.getTitle()).set(event);

        // add the ordering for the event
        Map<String, Object> docData = new HashMap<>();
        docData.put("order", eventList.size() - 1);
        usersCr.document(event.getTitle()).set(docData, SetOptions.merge());
    }

    /**
     * Method for deleting an event in our {@link FirebaseFirestore} database and in the {@link Event}
     * object list on the current fragment
     * This uses an index instead of an object name or object because a user can change the event's name
     * as this can be used for editing an event as well, so the index makes sure we reference the
     * correct event that we are changing/deleting in the list
     * @param index the current index of the item to delete.
     */
    public void deleteEvent(int index) {
        // initialize our Collection reference to the CurrentUser's collection
        final DocumentReference userDr = db.collection("users").document(currentUser);
        final CollectionReference usersCr = userDr.collection("Events");
        // get the event that we are deleting
        Event e = eventArrayAdapter.getItem(index);
        // remove it from the list
        eventList.remove(index);
        // render changes
        eventArrayAdapter.notifyDataSetChanged();
        // also delete from database
        usersCr.document(e.getTitle()).delete();

        Map<String, Object> docData = new HashMap<>();
        // update all further ordering for deletion
        for (int i = index; i < eventList.size(); i++) {
            // add the ordering for the event
            docData.put("order", i);
            usersCr.document(eventList.get(i).getTitle()).set(docData, SetOptions.merge());
        }
    }

    /**
     * Method for inserting an event to our {@link FirebaseFirestore} database as well as our {@link Event}
     * object list that's shown on the current fragment at a certain index
     * @param event the {@link Event} to be inserted to the database and listview
     * @param index the index that the habit is to be inserted into the list
     */
    public void insertEvent(Event event, int index) {
        // initialize our Collection reference to the CurrentUser's collection
        final DocumentReference userDr = db.collection("users").document(currentUser);
        final CollectionReference usersCr = userDr.collection("Events");
        // insert event into list
        eventList.add(index, event);
        // render changes
        eventArrayAdapter.notifyDataSetChanged();

        Map<String, Object> docData = new HashMap<>();
        // update all further ordering for insertion
        for (int i = index + 1; i < eventList.size(); i++) {
            // add the ordering for the event
            docData.put("order", i);
            usersCr.document(eventList.get(i).getTitle()).set(docData, SetOptions.merge());
        }

        // add our new event to the index in firebase
        usersCr.document(event.getTitle()).set(event);
        docData.put("order", index);
        usersCr.document(event.getTitle()).set(docData, SetOptions.merge());
    }
}