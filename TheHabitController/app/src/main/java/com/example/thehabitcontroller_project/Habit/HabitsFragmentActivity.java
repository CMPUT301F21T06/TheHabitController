package com.example.thehabitcontroller_project.Habit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thehabitcontroller_project.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A {@link Fragment} subclass to show the current {@link Habit}s of the user
 * This class links with {@link FirebaseFirestore} in order to store, pull and update its data
 *
 * @author Steven
 * @version 1.2.0
 */
public class HabitsFragmentActivity extends Fragment implements HabitRecyclerAdapter.OnHabitItemClickedListener {

    private List<Habit> habitList;
    private HabitRecyclerAdapter habitRecyclerAdapter;
    private RecyclerView habitRecyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final DocumentReference userDr = db.collection("users").document(currentUser);
    private final CollectionReference usersCr = userDr.collection("Habits");
    private final String DBTAG = "FireStore Call";
    private NavController navController;

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
        getActivity().setTitle("Habits List");

        // First grab reference to all the UI objects we will be using
        FloatingActionButton fab = view.findViewById(R.id.habitFloatingActionButton);
        navController = Navigation.findNavController(view);
        db = FirebaseFirestore.getInstance();
        habitList = new ArrayList<>();
        habitRecyclerAdapter = new HabitRecyclerAdapter(habitList, this);
        habitRecyclerView = view.findViewById(R.id.habit_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        habitRecyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.Callback callback = new HabitItemTouchHelper(habitRecyclerAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        habitRecyclerAdapter.setTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(habitRecyclerView);

        habitRecyclerView.setAdapter(habitRecyclerAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(habitRecyclerView.getContext(),
                layoutManager.getOrientation());
        habitRecyclerView.addItemDecoration(dividerItemDecoration);

        // initialize our habit list from the FireStore database
        initializeHabitList(view);

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
        // update habit information
        updateHabitsInfo();

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
                habitRecyclerAdapter.notifyDataSetChanged();

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

//        // listener for if changes occur in our FireStore db, then we change it accordingly here
//        usersCr.orderBy("order").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error == null && value != null){
//                    for (DocumentChange dc : value.getDocumentChanges()) {
//                        Habit h = dc.getDocument().toObject(Habit.class);
//                        if (h == null) { continue; }
//                        if (dc.getType() == DocumentChange.Type.ADDED && !habitList.contains(h)) {
//                            habitList.add(h);
//                            habitRecyclerAdapter.notifyItemInserted(habitList.size()-1);
//                        }
//                        else if (dc.getType() == DocumentChange.Type.MODIFIED) {
//                            int index = habitList.indexOf(h);
//                            habitList.remove(index);
//                            habitList.add(index, h);
//                            habitRecyclerAdapter.notifyItemChanged(index);
//                        }
//                        else if (dc.getType() == DocumentChange.Type.REMOVED){
//                            int index = habitList.indexOf(h);
//                            habitList.remove(h);
//                            habitRecyclerAdapter.notifyItemRemoved(index);
//                        }
//                    }
//                }
//                else {
//                    Log.d(DBTAG, "Could not get the snapshot of db changes or value was Null");
//                }
//            }
//        });
    }

    /**
     * A helper method for updating habits information as they are read into this list
     *
     * Things this method updates for:
     *    - The total number of times the habit has shown up in the daily habits list
     */
    private void updateHabitsInfo() {
        // update Habits that are supposed to be done today by updating their "totalShownTimes" field
        usersCr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // get the date of week index
                int dowIndex = LocalDate.now().getDayOfWeek().getValue() - 1;
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    Habit h = doc.toObject(Habit.class);
                    // if the habit's schedule matches today's date
                    if (h.getSchedule().get(dowIndex)) {
                        // calculate the total number of days
                        Date habitStartDate = h.getDateStart();
                        habitStartDate.getTime();
                        int total = (int) getTotalNumDays(habitStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), h.getSchedule());
                        // if the total is different, update this value for the habit
                        if (total != h.getTotalShownTimes()) {
                            usersCr.document(h.getTitle()).update(h.getTotalShownTimesString(), total);
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we fail calling to FireStore, log it under our DBTAG
                Log.d(DBTAG, "Was not able to get the data from Firestore to populate initial Habit list");
            }
        });
    }

    /**
     * Gets the total number of days the habit was supposed to be shown between a start date and today's date
     * while ignoring days that are not part of the {@link Habit}'s schedule
     * @param start     the {@link LocalDate} start date
     * @param schedule  the {@link List<Boolean>} schedule of the {@link Habit}
     * @return
     */
    private static long getTotalNumDays(LocalDate start, List<Boolean> schedule) {
        // get the DayOfWeek that we're supposed to ignore
        List<DayOfWeek> ignore = new ArrayList<>();
        for (int i = 0; i < schedule.size(); i++) {
            if (!schedule.get(i)) {
                DayOfWeek dow = DayOfWeek.of(i + 1);
                ignore.add(dow);
            }
        }
        // end date is today's date
        LocalDate end = LocalDate.now();
        // iterate over all days and grab the count
        return Stream.iterate(start, d->d.plusDays(1))
                .limit(start.until(end, ChronoUnit.DAYS))
                .filter(d->!ignore.contains(d.getDayOfWeek()))
                .count();
    }

    /**
     * Method for adding a habit to our {@link FirebaseFirestore} database as well as our {@link Habit}
     * object list that's shown on the current fragment.
     * @param habit the {@link Habit} to be added to the database and listview
     */
    public void addHabit(Habit habit) {
        // add the habit to our list
        habitList.add(habit);
        // render changes
        habitRecyclerAdapter.notifyItemInserted(habitList.size()-1);
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
        // get the habit that we are deleting
        Habit h = habitList.get(index);
        // also delete from database
        usersCr.document(h.getTitle()).delete();
        // remove it from the list
        habitList.remove(index);
        // render changes
        habitRecyclerAdapter.notifyItemRemoved(index);

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
        // insert habit into list
        habitList.add(index, habit);
        // render changes
        habitRecyclerAdapter.notifyItemInserted(index);

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

    @Override
    public void onHabitClick(int position) {
        Bundle editHabitBundle = new Bundle();
        Habit h = habitList.get(position);
        editHabitBundle.putParcelable("Habit", h);
        editHabitBundle.putInt("index", position);
        navController.navigate(R.id.action_habits_to_editHabitFragmentActivity, editHabitBundle);
    }
}