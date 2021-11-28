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
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A {@link Fragment} subclass to show the daily {@link Habit}s that the {@link User} plans
 * to do.
 *
 * @author Steven
 * @version 1.0.0
 */
public class HomeFragmentActivity extends Fragment {

    private final int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
    private FirebaseFirestore db;
    private String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private List<Habit> dailyHabitList;
    private ListView dailyHabitsListView;
    private DailyHabitArrayAdapter dailyHabitArrayAdapter;
    private final String DBTAG = "FireStore Call";

    public HomeFragmentActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Today's Habits");

        // get our initial field values
        NavController navController = Navigation.findNavController(view);
        db = FirebaseFirestore.getInstance();
        dailyHabitList = new ArrayList<>();
        dailyHabitsListView = view.findViewById(R.id.daily_habits_list);
        dailyHabitArrayAdapter = new DailyHabitArrayAdapter(view.getContext(), dailyHabitList);
        dailyHabitsListView.setAdapter(dailyHabitArrayAdapter);

        // initialize our habit list that we need to do today
        initializeTodayHabitList(view);

        // listener for if our habit was done
        dailyHabitsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // send over to AddHabitEventActivity to handle
                Bundle habitBundle = new Bundle();
                Habit h = dailyHabitArrayAdapter.getItem(i);
                habitBundle.putParcelable("DailyHabit", h);
                navController.navigate(R.id.action_home2_to_events, habitBundle);
            }
        });
    }

    /**
     * A helper method for setting up initializing our list of today's {@link Habit}
     * objects. This method will look at our {@link FirebaseFirestore} database and
     * build our list from the entries that exist in that collection.
     * @param view the current {@link View} we are in
     */
    private void initializeTodayHabitList(View view) {
        // get our references to the database
        final DocumentReference userDr = db.collection("users").document(currentUser);
        final CollectionReference usersCr = userDr.collection("Habits");

        // get all habit documents of our user
        usersCr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // initialize our week index
                int dayOfWeekIndex = dayOfWeek - 1;
                dailyHabitList.clear();
                // go through all habits of this user
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    Habit habit = doc.toObject(Habit.class);
                    // if the habit is supposed to be scheduled for today, add to our daily list
                    if (habit.getSchedule() != null && habit.getSchedule().get(dayOfWeekIndex)) {
                        dailyHabitList.add(habit);
                    }
                }
                // re-render our listview
                dailyHabitArrayAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we fail calling to FireStore, log it under our DBTAG
                Log.d(
                    DBTAG,
                    "Was not able to get the data from Firestore to populate Today's Habits"
                );
            }
        });
    }
}