package com.example.thehabitcontroller_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ClipData;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.thehabitcontroller_project.Habit.Habit;
import com.example.thehabitcontroller_project.Habit.HabitRecyclerAdapter;
import com.firebase.ui.auth.ui.idp.SingleSignInActivity;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The UI testing of Habit Fragment classes done with {@link Solo} from Robotium
 */
public class TestHabitFragments {
    private Solo solo;
    private final String currentUsername = "0sHG9jXwBzPiqaJWP0Xf2uTiDRI2";
    private final String userName = "john@gmail.com";
    private final String pass = "asdf1234";

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Initialize our {@link Solo} object for intent testing
     * Also handles logging in past the initial login screen
     * @throws Exception
     */
    @Before
    public void setUpLogin() throws Exception {
        // setup Solo
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        // navigate through email entering if prompted
        if (solo.searchText("E-mail")) {
            solo.enterText((EditText) solo.getView(R.id.tiLoginEmailTextField), userName);
            solo.enterText((EditText) solo.getView(R.id.tiLoginPasswordTextField), pass);

            // for some reason, clickonbutton and clickOnView does not work... so we go hacky
            solo.clickOnButton(0);
        }
    }

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * Test that we can navigate to the habit list fragment, and get its list
     */
    @Test
    public void TestHabitFragmentActivity() throws InterruptedException {
        // first we are in Main Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        // make sure we don't have the habits list initially in the main view
        assertNull(solo.getCurrentActivity().findViewById(R.id.habit_list));
        // click on the habits menu UI
        solo.clickOnView(solo.getView(R.id.habits));
        // wait for fragment and get our habit list on the habit fragment activity
        solo.waitForFragmentById(R.id.habits, 2000);
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.habit_list));
    }

    /**
     * Test that we can navigate to the add habit fragment from the habit list fragment
     * as well as add a new habit to our list
     */
    @Test
    public void TestAddHabitFragmentActivity() {
        // navigate to the habit activity
        solo.clickOnView(solo.getView(R.id.habits));
        // wait for fragment and click on the add button
        solo.waitForFragmentById(R.id.habits, 2000);

        // make sure we're in the habits fragment activity first
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.habits));

        // assert that we do not have this habit in our initial list
        String testHabit = "Eat fibre";
        assertTrue(!solo.searchText(testHabit));

        // click on the add habit button
        solo.clickOnView(solo.getView(R.id.habitFloatingActionButton));
        solo.waitForFragmentById(R.id.addHabitActivity, 2000);
        // can't find the actual fragment using Solo, so we search for the unique 'Add' button
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.addHabitButton));

        // now we add to our list
        solo.enterText((EditText) solo.getView(R.id.habit_title), testHabit);
        solo.enterText((EditText) solo.getView(R.id.habit_reason), "Need to get healthier");
        // click add button
        solo.clickOnView(solo.getView(R.id.addHabitButton));

        // assert that the entry is in the list
        solo.waitForText(testHabit);
        assertTrue(solo.searchText(testHabit));

        // cleanup db
        removeFromFireBase(testHabit, currentUsername);
    }

    /**
     * This tests the editing functionality of habits in the habit list
     */
    @Test
    public void TestEditHabitFragmentActivity() {
        // navigate to the habit activity
        solo.clickOnView(solo.getView(R.id.habits));
        // wait for fragment and click on the add button
        solo.waitForFragmentById(R.id.habits, 2000);

        String editedHabitName = " more";
        // make sure we're in the habits fragment activity first
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.habits));
        // also make sure we dont have our edited habit in the initial list
        assertFalse(solo.searchText(editedHabitName));

        // click on the add habit button
        solo.clickOnView(solo.getView(R.id.habitFloatingActionButton));
        solo.waitForFragmentById(R.id.addHabitActivity, 2000);

        // now we add to our list
        String testHabitName = "Go Run";
        solo.enterText((EditText) solo.getView(R.id.habit_title), testHabitName);
        solo.enterText((EditText) solo.getView(R.id.habit_reason), "Need to exercise more");
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        solo.clickOnView(solo.getView(R.id.addHabitButton));
        solo.waitForText(testHabitName);

        // click on entry in our list
        solo.clickOnText(testHabitName);
        // wait until we are in the edit fragment
        solo.waitForFragmentById(R.id.editHabitFragmentActivity, 2000);
        // assert that we see the unique save button only on edit page
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.saveHabitButton));
        // now edit our habit with added portion and save
        solo.enterText((EditText) solo.getView(R.id.habit_title), editedHabitName);
        solo.clickOnView(solo.getView(R.id.saveHabitButton));

        // wait until we're back to our habits page and make sure we now see our edited habit
        solo.waitForFragmentById(R.id.habits, 2000);
        assertTrue(solo.searchText(testHabitName + editedHabitName));

        // cleanup
        removeFromFireBase(testHabitName + editedHabitName, currentUsername);
    }

    /**
     * Tests the deletion of habits from the habit list
     */
    @Test
    public void TestHabitListDeletion() {
        // navigate to the habit activity
        solo.clickOnView(solo.getView(R.id.habits));
        // wait for fragment and click on the add button
        solo.waitForFragmentById(R.id.habits, 2000);

        // make sure we're in the habits fragment activity first
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.habits));

        // click on the add habit button
        solo.clickOnView(solo.getView(R.id.habitFloatingActionButton));
        solo.waitForFragmentById(R.id.addHabitActivity, 2000);

        // now we add to our list
        String testHabit = "Go Jog";
        solo.enterText((EditText) solo.getView(R.id.habit_title), testHabit);
        solo.enterText((EditText) solo.getView(R.id.habit_reason), "Need exercise");
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        solo.clickOnView(solo.getView(R.id.addHabitButton));
        solo.waitForText(testHabit);

        // click on entry in our list
        solo.clickOnText(testHabit);
        // wait until we are in the edit fragment
        solo.waitForFragmentById(R.id.editHabitFragmentActivity, 2000);
        // assert that we see the unique delete button only on edit page
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.deleteHabitButton));
        // click on delete button
        solo.clickOnView(solo.getView(R.id.deleteHabitButton));

        // wait until we're back to our habits page
        solo.waitForFragmentById(R.id.habits, 2000);
        // assert that we don't see our habit anymore
        assertFalse(solo.searchText(testHabit));
    }

    /**
     * Helper method for adding a habit so that we can see if it shows up in the daily habits
     * / home page
     * @param habit the habit with the information you want to add for testing
     */
    private void addAHabit(Habit habit) {
        // navigate to the habit activity
        solo.clickOnView(solo.getView(R.id.habits));
        // wait for fragment and click on the add button
        solo.waitForFragmentById(R.id.habits, 2000);

        // make sure we're in the habits fragment activity first
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.habits));

        // if we have this habit already, don't do anything
        if (solo.searchText(habit.getTitle())) {
            return;
        }

        // click on the add habit button
        solo.clickOnView(solo.getView(R.id.habitFloatingActionButton));
        solo.waitForFragmentById(R.id.addHabitActivity, 2000);
        // set the habit's schedule
        ChipGroup chipGroup = (ChipGroup) solo.getView(R.id.weekly_chip_group);
        for (int i = 0; i < chipGroup.getChildCount() && i < habit.getSchedule().size(); i++) {
            if (habit.getSchedule().get(i)) {
                solo.clickOnView(chipGroup.getChildAt(i));
            }
        }
        // can't find the actual fragment using Solo, so we search for the unique 'Add' button
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.addHabitButton));

        // now we add to our list
        solo.enterText((EditText) solo.getView(R.id.habit_title), habit.getTitle());
        solo.enterText((EditText) solo.getView(R.id.habit_reason), habit.getReason());
        // click add button
        solo.clickOnView(solo.getView(R.id.addHabitButton));

        // assert that the entry is in the list
        solo.waitForText(habit.getTitle());
        assertTrue(solo.searchText(habit.getTitle()));
    }

    /**
     * A method for cleaning up firebase additions
     * @param testHabit the habit name to clean up
     * @param userName the user whose habit is associated with
     */
    private void removeFromFireBase(String testHabit, String userName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cr = db.collection("users").document(userName).collection("Habits");
        cr.document(testHabit).delete();
    }
}
