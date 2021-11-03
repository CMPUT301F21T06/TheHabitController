package com.example.thehabitcontroller_project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass to handle fragments the user added.
 * create an instance of this fragment.
 */
public class HabitsFragmentActivity extends Fragment {
    List<Habit> habitList;
    ArrayAdapter<Habit> habitArrayAdapter;
    ListView habitListView;

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

        habitListView = view.findViewById(R.id.habit_list);
        habitList = new ArrayList();
        habitArrayAdapter = new habitArrayAdapter(view.getContext(), habitList);
        habitListView.setAdapter(habitArrayAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_habits_to_addHabitActivity);
            }
        });

        Bundle addHabitBundle = getArguments();
        if (!addHabitBundle.isEmpty()) {
            addHabit(addHabitBundle.getParcelable("addHabit"));
        }
    }

    public void addHabit(Habit habit) {
        habitArrayAdapter.add(habit);
    }
}