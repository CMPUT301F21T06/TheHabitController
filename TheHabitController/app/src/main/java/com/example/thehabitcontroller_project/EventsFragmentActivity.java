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
 * A simple {@link Fragment} subclass.
 * Use the {@link EventsFragmentActivity#newInstance} factory method to
 * create an instance of this fragment.
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

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment Events.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static EventsFragmentActivity newInstance(String param1, String param2) {
//        EventsFragmentActivity fragment = new EventsFragmentActivity();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = view.findViewById(R.id.eventFloatingActionButton);
        NavController navController = Navigation.findNavController(view);
        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();
        eventArrayAdapter = new eventArrayAdapter(view.getContext(), eventList);
        eventListView = view.findViewById(R.id.event_list);
        eventListView.setAdapter(eventArrayAdapter);

        initializeEventList(view);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_events_to_addEventActivity);
            }
        });

        Bundle addEventBundle = getArguments();
        if (!addEventBundle.isEmpty()) {
            addEvent(addEventBundle.getParcelable("addEvent"));
        }
    }

    private void initializeEventList(View view) {
        final DocumentReference userDr = db.collection("users").document(currentUser);
        final CollectionReference usersCr = userDr.collection("Events");

        usersCr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                eventList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    eventList.add(doc.toObject(Event.class));
                }
                eventArrayAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(DBTAG, "Was not able to get the data from Firestore to populate initial Event list");
            }
        });

        usersCr.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                evenList.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    eventList.add(doc.toObject(Event.class));
                }
                eventArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    public void addEvent(Event event) {
        final DocumentReference userDr = db.collection("users").document(currentUser);
        final CollectionReference usersCr = userDr.collection("Events");

        eventArrayAdapter.add(event);
        usersCr.add(event);
    }
}