package com.example.thehabitcontroller_project;

import static androidx.navigation.Navigation.findNavController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.thehabitcontroller_project.Community.CommunityFragmentActivity;
import com.example.thehabitcontroller_project.Community.User;
import com.example.thehabitcontroller_project.Event.EventsFragmentActivity;
import com.example.thehabitcontroller_project.Habit.AddHabitFragmentActivity;
import com.example.thehabitcontroller_project.Habit.Habit;
import com.example.thehabitcontroller_project.Habit.HabitsFragmentActivity;
import com.example.thehabitcontroller_project.Home.HomeFragmentActivity;
import com.example.thehabitcontroller_project.Login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * @author mainul1
 * @version 1.0.0.0
 * This is our main activity class which will connect everything, make an interface
 * and fragments and connect all the fragments
 * @see HomeFragmentActivity
 * @see HabitsFragmentActivity
 * @see Habit
 * @see EventsFragmentActivity
 * @see CommunityFragmentActivity
 * @see AddHabitFragmentActivity
 */
public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    NavController navController;

    @Override
    /**
     * @param savedInstanceState will get the Bundle null when activity get starts first time and it will get in use when activity get changed
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpNavigation();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (User.getCurrentUser()==null) {
            signIn();
        }
    }

    /**
     * Build and launch login intent
     */
    public void signIn() {
        Log.d("LoginDetect","Start Login Activity");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * This function will create the navigation fragment on the bottom and connect all the fragments
     */
    public void setUpNavigation() {
        /**
         * @param bottomNavigationView creates the bottom navigation
         */
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
        navController = navHostFragment.getNavController();
        //For changing title
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}