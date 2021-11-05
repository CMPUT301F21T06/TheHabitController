package com.example.thehabitcontroller_project;

import static androidx.navigation.Navigation.findNavController;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

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

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    if (result.getResultCode()==RESULT_OK){
                        Log.d("SignIn","Activity result");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        User.setCurrentUser(new User(user));
                        if (result.getIdpResponse().isNewUser()){
                            Log.d("SignIn","Found new user");
                            User.firstLogin();
                            Log.d("SignIn",User.getCurrentUser().getUserName());
                        }
                    }
                }
            }
    );

    @Override
    /**
     * @param savedInstanceState will get the Bundle null when activity get starts first time and it will get in use when activity get changed
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpNavigation();
        // register firebase authentication listener for login/logout operations
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()!=null){
                    Log.d("SignIn","AuthStateChanged");
                } else {
                    signIn();
                }
            }
        });
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
        Intent loginIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();
        signInLauncher.launch(loginIntent);
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