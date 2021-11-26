package com.example.thehabitcontroller_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import java.util.ArrayList;

public class UserSearchActivity extends AppCompatActivity {
    private UserArrayAdapter userArrayAdapter;
    private ArrayList<User> userArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setSubtitle("User Search");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // init rvSearch
        RecyclerView rvSearch = findViewById(R.id.rvSearch);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        rvSearch.setLayoutManager(layoutManager);
        userArrayList = new ArrayList<>();
        UserArrayAdapter.ClickListener clickListener = new UserArrayAdapter.ClickListener() {
            @Override
            public void onItemClick(int pos, User itemUser) {

            }
        };
        userArrayAdapter = new UserArrayAdapter(this, userArrayList, clickListener);
        rvSearch.setAdapter(userArrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        menuItem.expandActionView();
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                User.searchUser(query, new User.UserSearchListener() {
                    @Override
                    public void onSearchComplete(ArrayList<User> result) {
                        userArrayList.clear();
                        userArrayList.addAll(result);
                        userArrayAdapter.notifyDataSetChanged();
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This method is called whenever the user chooses to navigate Up within your application's
     * activity hierarchy from the action bar.
     *
     * @return true if Up navigation completed successfully and this Activity was finished,
     * false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}