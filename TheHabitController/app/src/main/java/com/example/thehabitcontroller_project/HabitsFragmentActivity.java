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
 * A {@link Fragment} subclass to show the current {@link Habit}s of the user
 * This class links with {@link FirebaseFirestore} in order to store, pull and update its data
 *
 * @author Steven
 * @version 1.1.0
 */
public class HabitsFragmentActivity extends Fragment {

    private List<Habit> habitList;
    private ArrayAdapter<Habit> habitArrayAdapter;
    private ListView habitListView;
    private FirebaseFirestore db;
    private String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    final String DBTAG = "FireStore Call";

    public HabitsFragmentActivity() {
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
        return inflater.inflate(R.layout.fragment_habits, container, false);
    }

    /**
     * Override for extending the {@link Fragment} class that just
     * calls its parent's implementation of onCreate()
     */
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    /**
     * Override for extending the {@link Fragment} class for handling
     * building the structures after the view is created
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // First grab reference to all the UI objects we will be using
        FloatingActionButton fab = view.findViewById(R.id.habitFloatingActionButton);
        NavController navController = Navigation.findNavController(view);
        db = FirebaseFirestore.getInstance();
        habitList = new ArrayList<>();
        habitArrayAdapter = new HabitArrayAdapter(view.getContext(), habitList);
        habitListView = view.findViewById(R.id.habit_list);
        habitListView.setAdapter(habitArrayAdapter);

        // initialize our habit list from the FireStore database
        initializeHabitList(view);

        // set the listener for any user tapping on an item in the list of Habits
        habitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle editHabitBundle = new Bundle();
                Habit h = habitArrayAdapter.getItem(i);
                editHabitBundle.putParcelable("Habit", h);
                editHabitBundle.putInt("index", i);
                navController.navigate(R.id.action_habits_to_editHabitFragmentActivity, editHabitBundle);
            }
        });

        // set the listener for our button that adds new Habits to the list
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_habits_to_addHabitActivity);
            }
        });
    }

    /**
     * A helper method for organizing when we return from adding or editing a {@link Habit} in the list
     * This will go through all possible {@link Bundle} objects to see what to do if a user
     * has edited, added or deleted a {@link Habit}
     */
    private void checkHabitListChanges() {
        // first get the bundle from the arguments
        Bundle currBundle = getArguments();
        // if it's empty, then we do nothing
        if (!currBundle.isEmpty()) {
            // see if we've added a Habit
            Habit addHabit = currBundle.getParcelable("addHabit");
            if (addHabit != null) {
                addHabit(addHabit);
            }

            // see if we have a habit that was edited
            Habit editedHabit = currBundle.getParcelable("editedHabit");
            if (editedHabit != null) {
                int index = currBundle.getInt("index");
                deleteHabit(index);
                insertHabit(editedHabit, index);
            }

            // see if we've deleted a habit
            int deleteIndex = currBundle.getInt("deleteIndex", -1);
            if (deleteIndex != -1) {
                deleteHabit(deleteIndex);
            }

            // clear args so we refresh for next commands
            getArguments().clear();
        }
    }

    /**
     * A helper method for setting up initializing our list of {@link Habit} objects
     * This method will look at our {@link FirebaseFirestore} database and build our
     * list from the entries that exist in that collection.
     * @param view the current {@link View} we are in
     */
    private void initializeHabitList(View view) {
        // initialize our Collection reference to the CurrentUser's collection
        final DocumentReference userDr = db.collection("users").document(currentUser);
        final CollectionReference usersCr = userDr.collection("Habits");

        // set the listener for when the async call finishes
        usersCr.orderBy("order").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // clear our habit list if there is anything in it
                habitList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    // add all habits to the list
                    habitList.add(doc.toObject(Habit.class));
                }
                // render changes
                habitArrayAdapter.notifyDataSetChanged();

                // check here if we've come back from editing/adding habits and make changes
                checkHabitListChanges();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we fail calling to FireStore, log it under our DBTAG
                Log.d(DBTAG, "Was not able to get the data from Firestore to populate initial Habit list");
            }
        });

        // listener for if changes occur in our FireStore db, then we change it accordingly here
        usersCr.orderBy("order").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                habitList.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    habitList.add(doc.toObject(Habit.class));
                }
                habitArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Method for adding a habit to our {@link FirebaseFirestore} database as well as our {@link Habit}
     * object list that's shown on the current fragment.
     * @param habit the {@link Habit} to be added to the database and listview
     */
    public void addHabit(Habit habit) {
        // initialize our Collection reference to the CurrentUser's collection
        final DocumentReference userDr = db.collection("users").document(currentUser);
        final CollectionReference usersCr = userDr.collection("Habits");
        // add the habit to our list
        habitList.add(habit);
        // render changes
        habitArrayAdapter.notifyDataSetChanged();
        // add habit to our database
        usersCr.document(habit.getTitle()).set(habit);

        // add the ordering for the habit
        Map<String, Object> docData = new HashMap<>();
        docData.put("order", habitList.size() - 1);
        usersCr.document(habit.getTitle()).set(docData, SetOptions.merge());
    }

    /**
     * Method for deleting a habit to our {@link FirebaseFirestore} database and in the {@link Habit}
     * object list on the current fragment
     * This uses an index instead of an object name or object because a user can change the habit's name
     * as this can be used for editing a habit as well, so the index makes sure we reference the
     * correct habit that we are changing/deleting in the list
     * @param index the current index of the item to delete.
     */
    public void deleteHabit(int index) {
        // initialize our Collection reference to the CurrentUser's collection
        final DocumentReference userDr = db.collection("users").document(currentUser);
        final CollectionReference usersCr = userDr.collection("Habits");
        // get the habit that we are deleting
        Habit h = habitArrayAdapter.getItem(index);
        // remove it from the list
        habitList.remove(index);
        // render changes
        habitArrayAdapter.notifyDataSetChanged();
        // also delete from database
        usersCr.document(h.getTitle()).delete();

        Map<String, Object> docData = new HashMap<>();
        // update all further ordering for deletion
        for (int i = index; i < habitList.size(); i++) {
            // add the ordering for the habit
            docData.put("order", i);
            usersCr.document(habitList.get(i).getTitle()).set(docData, SetOptions.merge());
        }
    }

    /**
     * Method for inserting a habit to our {@link FirebaseFirestore} database as well as our {@link Habit}
     * object list that's shown on the current fragment at a certain index
     * @param habit the {@link Habit} to be inserted to the database and listview
     * @param index the index that the habit is to be inserted into the list
     */
    public void insertHabit(Habit habit, int index) {
        // initialize our Collection reference to the CurrentUser's collection
        final DocumentReference userDr = db.collection("users").document(currentUser);
        final CollectionReference usersCr = userDr.collection("Habits");
        // insert habit into list
        habitList.add(index, habit);
        // render changes
        habitArrayAdapter.notifyDataSetChanged();

        Map<String, Object> docData = new HashMap<>();
        // update all further ordering for insertion
        for (int i = index + 1; i < habitList.size(); i++) {
            // add the ordering for the habit
            docData.put("order", i);
            usersCr.document(habitList.get(i).getTitle()).set(docData, SetOptions.merge());
        }

        // add our new habit to the index in firebase
        usersCr.document(habit.getTitle()).set(habit);
        docData.put("order", index);
        usersCr.document(habit.getTitle()).set(docData, SetOptions.merge());
    }
}