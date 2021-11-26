package com.example.thehabitcontroller_project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityFragmentActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityFragmentActivity extends Fragment {

    private UserArrayAdapter userArrayAdapter;

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
        ArrayList<User> userArrayList=new ArrayList<>();

        // item click listener
        UserArrayAdapter.ClickListener clickListener = new UserArrayAdapter.ClickListener() {
            @Override
            public void onItemClick(int pos, User itemUser) {

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
    }
}