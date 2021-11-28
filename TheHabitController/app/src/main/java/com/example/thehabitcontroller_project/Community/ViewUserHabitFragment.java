package com.example.thehabitcontroller_project.Community;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import com.example.thehabitcontroller_project.Habit.Habit;
import com.example.thehabitcontroller_project.R;

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
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
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
        usersCr.orderBy("order").get().addOnSuccessListener(queryDocumentSnapshots -> {
            // clear our habit list if there is anything in it
            habitList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                // add all habits to the list
                habitList.add(doc.toObject(Habit.class));
            }
            // render changes
            habitRecyclerAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.d(TAG, "Failed to initialize habit list"));
    }

    @Override
    public void onHabitClick(int position) {
        // view habit event list
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     *
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG,"MenuItem:"+item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}