package com.example.thehabitcontroller_project;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.thehabitcontroller_project.Habit.Habit;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * The UI testing of the Daily Habits Fragment classe done with {@link Solo} from Robotium
 */
public class TestDailyHabitFragment {

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
     *
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
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * Test that we load into to the daily habit list fragment, and get its list
     */
    @Test
    public void TestDailyHabitHomeFragmentActivity() throws InterruptedException {
        // first we are in Main Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        // wait for fragment to load and get our daily habit list on the daily habit/home fragment activity
        solo.waitForFragmentById(R.id.home2, 2000);
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.daily_habits_list));
    }

    /**
     * Test that adding a activity will show up properly (set it to appear every day of the week)
     */
    @Test
    public void TestDailyHabitShows() throws InterruptedException {
        // first make sure we are in home first
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.daily_habits_list));

        // make our new habit
        List<Boolean> schedule = new ArrayList<Boolean>(Arrays.asList(new Boolean[7]));
        Collections.fill(schedule, Boolean.TRUE);
        Habit h = new Habit("Eat 3 meals", "Want to be healthier", new Date(), true, schedule);

        // make sure it doesn't already exist in the daily habits to do list
        assertTrue(!solo.searchText(h.getTitle()));

        // add our habit
        addAHabit(h);

        // navigate back to the daily habit/home activity
        solo.clickOnView(solo.getView(R.id.home2));
        // wait for fragment
        solo.waitForFragmentById(R.id.home2, 2000);

        // make sure we can see the habit appear in the list that we just added
        assertTrue(solo.searchText(h.getTitle()));

        // cleanup
        removeFromFireBase(h.getTitle(), this.currentUsername);
    }

    /**
     * Helper method for adding a habit so that we can see if it shows up in the daily habits
     * / home page
     *
     * @param habit the habit with the information you want to add for testing
     */
    private void addAHabit(Habit habit) {
        // navigate to the habit activity
        solo.clickOnView(solo.getView(R.id.habits));
        // wait for fragment and click on the add button
        solo.waitForFragmentById(R.id.habits, 2000);

        // make sure we're in the habits fragment activity first
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.habits));

        // assert that we do not have this habit in our initial list
        assertTrue(!solo.searchText(habit.getTitle()));

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
     *
     * @param testHabit the habit name to clean up
     * @param userName the user whose habit is associated with
     */
    private void removeFromFireBase(String testHabit, String userName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cr = db.collection("users").document(userName).collection("Habits");
        cr.document(testHabit).delete();
    }
}
