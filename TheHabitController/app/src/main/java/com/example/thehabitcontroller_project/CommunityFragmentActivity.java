package com.example.thehabitcontroller_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommunityFragmentActivity extends Fragment {

    private UserArrayAdapter userArrayAdapter;
    private ArrayList<User> userArrayList;

    public CommunityFragmentActivity() {
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
        return inflater.inflate(R.layout.fragment_community, container, false);
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
        RecyclerView rvFollowing = view.findViewById(R.id.rvFollowing);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        rvFollowing.setLayoutManager(layoutManager);
        userArrayList=new ArrayList<>();

        // item click listener
        UserArrayAdapter.ClickListener clickListener = new UserArrayAdapter.ClickListener() {
            @Override
            public void onItemClick(int pos, User itemUser) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("user",itemUser);
                NavController navController= Navigation.findNavController(view);
                navController.navigate(R.id.action_community_to_viewUserHabitFragment,bundle);
            }
        };

        userArrayAdapter= new UserArrayAdapter(getContext(), userArrayList, clickListener);
        rvFollowing.setAdapter(userArrayAdapter);
        User.getCurrentUser().getFollowing(new User.UserListDataListener() {
            @Override
            public void onDataChange(ArrayList<User> result) {
                userArrayList.clear();
                userArrayList.addAll(result);
                userArrayAdapter.notifyDataSetChanged();
            }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvFollowing.getContext(),
                layoutManager.getOrientation());
        rvFollowing.addItemDecoration(dividerItemDecoration);

        FloatingActionButton fabSearch = view.findViewById(R.id.fabSearch);
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),UserSearchActivity.class);
                startActivity(intent);
            }
        });
    }
}