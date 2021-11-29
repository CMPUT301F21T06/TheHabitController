package com.example.thehabitcontroller_project;

import static androidx.navigation.Navigation.findNavController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.thehabitcontroller_project.Community.CommunityFragmentActivity;
import com.example.thehabitcontroller_project.Community.User;
import com.example.thehabitcontroller_project.Event.EventsFragmentActivity;
import com.example.thehabitcontroller_project.Habit.AddHabitFragmentActivity;
import com.example.thehabitcontroller_project.Habit.Habit;
import com.example.thehabitcontroller_project.Habit.HabitsFragmentActivity;
import com.example.thehabitcontroller_project.Home.HomeFragmentActivity;
import com.example.thehabitcontroller_project.Login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

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

    final private String TAG = "MainActivity";
    private ListenerRegistration lr;
    private List<String> followReqId = new ArrayList<>();


    @Override
    /**
     * @param savedInstanceState will get the Bundle null when activity get starts first time and it will get in use when activity get changed
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (User.getCurrentUser()==null) {
            signIn();
        }
        setContentView(R.layout.activity_main);
        setUpNavigation();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (User.getCurrentUser()==null) {
            signIn();
        } else {
            // notificationCounter = new NotificationCounter(findViewById(R.id.bell));
            if (lr==null){
                DocumentReference docRef= FirebaseFirestore.getInstance().collection("users")
                        .document(User.getCurrentUser().getUserId());
                lr=docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Database Listen failed.", error);
                            return;
                        }
                        String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                                ? "Local" : "Server";
                        if (snapshot!=null&&!snapshot.getMetadata().hasPendingWrites()){
                            // listen to remote changes only
                            Log.d(TAG,"Remote database change occurred.");
                            Log.d(TAG, source + " data: " + snapshot.getData().get("followReq"));
                            if (!followReqId.equals(snapshot.getData().get("followReq"))){
                                followReqId.clear();
                                if (snapshot.get("followReq")!=null){
                                    followReqId.addAll((List<String>) snapshot.get("followReq"));
                                    Log.d(TAG,"FRI:"+followReqId);
                                }
                            }
                        }
                    }
                });
            }
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

    /**
     * This method is called whenever the user chooses to navigate Up within your application's
     * activity hierarchy from the action bar.
     *
     * <p>If a parent was specified in the manifest for this activity or an activity-alias to it,
     * default Up navigation will be handled automatically. See
     * {@link #getSupportParentActivityIntent()} for how to specify the parent. If any activity
     * along the parent chain requires extra Intent arguments, the Activity subclass
     * should override the method {@link #onPrepareSupportNavigateUpTaskStack(TaskStackBuilder)}
     * to supply those arguments.</p>
     *
     * <p>See <a href="{@docRoot}guide/topics/fundamentals/tasks-and-back-stack.html">Tasks and
     * Back Stack</a> from the developer guide and
     * <a href="{@docRoot}design/patterns/navigation.html">Navigation</a> from the design guide
     * for more information about navigating within your app.</p>
     *
     * <p>See the {@link TaskStackBuilder} class and the Activity methods
     * {@link #getSupportParentActivityIntent()}, {@link #supportShouldUpRecreateTask(Intent)}, and
     * {@link #supportNavigateUpTo(Intent)} for help implementing custom Up navigation.</p>
     *
     * @return true if Up navigation completed successfully and this Activity was finished,
     * false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }

}





/***
 for marcus



 Button button; // By clicking follow,this button will activate

 notificationCounter = new NotificationCounter(findViewById(R.id.bell));
 button.setOnClickListner(new View.onClickListner()){
@Override
public void onClick(View view){
notificationCounter.increaseNumber(); // Will increase the number beside the notification until 99, if you feel like it, increase the number
}
});



 */


