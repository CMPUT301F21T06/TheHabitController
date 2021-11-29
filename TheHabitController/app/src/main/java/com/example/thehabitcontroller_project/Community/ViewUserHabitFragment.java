package com.example.thehabitcontroller_project.Community;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thehabitcontroller_project.Habit.Habit;
import com.example.thehabitcontroller_project.Habit.HabitRecyclerAdapter;
import com.example.thehabitcontroller_project.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewUserHabitFragment extends Fragment implements HabitRecyclerAdapter.OnHabitItemClickedListener {

    private User displayUser;
    private List<Habit> habitList;
    private HabitRecyclerAdapter habitRecyclerAdapter;
    private final String TAG="ViewUserHabit";
    private NavController navController;

    public ViewUserHabitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            displayUser=getArguments().getParcelable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_user_habit, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("View User Habits");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        // init NavController
        navController = Navigation.findNavController(view);

        // init RecyclerView
        RecyclerView rv = view.findViewById(R.id.rvViewUserHabit);
        habitList = new ArrayList<>();
        habitRecyclerAdapter = new HabitRecyclerAdapter(habitList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(habitRecyclerAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        // fetch info from database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDr = db.collection("users").document(displayUser.getUserId());
        CollectionReference usersCr = userDr.collection("Habits");
        Query q = usersCr.whereEqualTo("public",true);
        q.orderBy("order").get().addOnSuccessListener(queryDocumentSnapshots -> {
            // clear our habit list if there is anything in it
            habitList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                // add all habits to the list
                habitList.add(doc.toObject(Habit.class));
            }
            // render changes
            habitRecyclerAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.d(TAG, "Failed to initialize habit list"));

        FloatingActionButton fabUnfollow = view.findViewById(R.id.fabUnfollow);
        fabUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Unfollow")
                        .setMessage("Unfollow user \""+displayUser.getUserName()+"\"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                User.unfollow(displayUser);
                                Toast.makeText(getContext(),
                                        "User unfollowed.", Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void onHabitClick(int position) {
        // view habit event list
        Bundle bundle = new Bundle();
        Habit h = habitList.get(position);
        bundle.putParcelable("Habit", h);
        navController.navigate(R.id.action_viewUserHabitFragment_to_events, bundle);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}