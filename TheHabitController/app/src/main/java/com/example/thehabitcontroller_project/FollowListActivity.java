package com.example.thehabitcontroller_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class FollowListActivity extends AppCompatActivity {

    private UserArrayAdapter userArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list);
        RecyclerView rvFollowing = findViewById(R.id.rvFollowing);
        rvFollowing.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<User> userArrayList=new ArrayList<>();
        userArrayAdapter= new UserArrayAdapter(this, userArrayList);
        rvFollowing.setAdapter(userArrayAdapter);
        User.getCurrentUser().getFollowing(new User.UserListDataListener() {
            @Override
            public void onDataChange(ArrayList<User> result) {
                userArrayList.clear();
                userArrayList.addAll(result);
                userArrayAdapter.notifyDataSetChanged();
            }
        });
    }
}