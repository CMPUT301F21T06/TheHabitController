package com.example.thehabitcontroller_project;

import static androidx.navigation.Navigation.findNavController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

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
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration(R.id.home, R.id.events,R.id.habits,R.id.community).build();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}