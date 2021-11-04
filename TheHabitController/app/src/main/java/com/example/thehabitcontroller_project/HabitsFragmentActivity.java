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
 * A simple {@link Fragment} subclass to handle fragments the user added.
 * create an instance of this fragment.
 */
public class HabitsFragmentActivity extends Fragment {
    private List<Habit> habitList;
    private ArrayAdapter<Habit> habitArrayAdapter;
    private ListView habitListView;
    private FirebaseFirestore db;
    private String currentUser = "currentUser";
    final String DBTAG = "FireStore Call";

    public HabitsFragmentActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_habits, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = view.findViewById(R.id.habitFloatingActionButton);
        NavController navController = Navigation.findNavController(view);
        db = FirebaseFirestore.getInstance();
        habitList = new ArrayList<>();
        habitArrayAdapter = new habitArrayAdapter(view.getContext(), habitList);
        habitListView = view.findViewById(R.id.habit_list);
        habitListView.setAdapter(habitArrayAdapter);

        initializeHabitList(view);

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_habits_to_addHabitActivity);
            }
        });
    }

    private void checkHabitListChanges() {
        Bundle currBundle = getArguments();
        if (!currBundle.isEmpty()) {
            Habit addHabit = currBundle.getParcelable("addHabit");
            if (addHabit != null) {
                addHabit(addHabit);
            }

            Habit editedHabit = currBundle.getParcelable("editedHabit");
            if (editedHabit != null) {
                int index = currBundle.getInt("index");
                deleteHabit(index);
                addHabit(editedHabit);
            }

            int deleteIndex = currBundle.getInt("deleteIndex", -1);
            if (deleteIndex != -1) {
                deleteHabit(deleteIndex);
            }
        }
    }

    private void initializeHabitList(View view) {
        final DocumentReference userDr = db.collection("users").document(currentUser);
        final CollectionReference usersCr = userDr.collection("Habits");

        usersCr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                habitList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    habitList.add(doc.toObject(Habit.class));
                }
                habitArrayAdapter.notifyDataSetChanged();

                checkHabitListChanges();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(DBTAG, "Was not able to get the data from Firestore to populate initial Habit list");
            }
        });

        usersCr.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    public void addHabit(Habit habit) {
        final DocumentReference userDr = db.collection("users").document(currentUser);
        final CollectionReference usersCr = userDr.collection("Habits");
        habitList.add(habit);
        habitArrayAdapter.notifyDataSetChanged();
        usersCr.document(habit.getTitle()).set(habit);
    }

    public void deleteHabit(int index) {
        final DocumentReference userDr = db.collection("users").document(currentUser);
        final CollectionReference usersCr = userDr.collection("Habits");
        Habit h = habitArrayAdapter.getItem(index);
        habitList.remove(h);
        habitArrayAdapter.notifyDataSetChanged();
        usersCr.document(h.getTitle()).delete();
    }
}