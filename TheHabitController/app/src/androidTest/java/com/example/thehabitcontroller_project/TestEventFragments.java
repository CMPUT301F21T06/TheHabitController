package com.example.thehabitcontroller_project;

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
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.firebase.ui.auth.ui.idp.SingleSignInActivity;
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
import java.util.Date;
import java.util.Locale;

/**
 * The UI testing of Event Fragment classes done with {@link Solo} from Robotium
 */
public class TestEventFragments {
    private Solo solo;
    private final String currentUsername = "currentUser";
    private final String userName = "test@test.com";
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
        if (solo.searchText("Sign in")) {
            solo.enterText((EditText) solo.getView(R.id.email), userName);
            solo.clickOnView(solo.getView(R.id.button_next));

            // enter the password
            solo.enterText((EditText) solo.getView(R.id.password), pass);

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
     * Test that we can navigate to the event list fragment, and get its list
     */
    @Test
    public void TestEventFragmentActivity() throws InterruptedException {
        // first we are in Main Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        // make sure we don't have the habits list initially in the main view
        assertNull(solo.getCurrentActivity().findViewById(R.id.event_list));
        // click on the habits menu UI
        solo.clickOnView(solo.getView(R.id.events));
        // wait for fragment and get our habit list on the habit fragment activity
        solo.waitForFragmentById(R.id.events, 1000);
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.event_list));
    }

    /**
     * Test that we can navigate to the add event fragment from the event list fragment
     * as well as add a new event to our list
     */
    @Test
    public void TestAddEventFragmentActivity() {
        // navigate to the event activity
        solo.clickOnView(solo.getView(R.id.events));
        // wait for fragment and click on the add button
        solo.waitForFragmentById(R.id.events, 1000);

        // make sure we're in the events fragment activity first
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.events));

        // assert that we do not have this event in our initial list
        String testEvent = "Slept early";
        assertTrue(!solo.searchText(testEvent));

        // click on the add event button
        solo.clickOnView(solo.getView(R.id.eventFloatingActionButton));
        solo.waitForFragmentById(R.id.addEventActivity, 1000);
        // can't find the actual fragment using Solo, so we search for the unique 'Add' button
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.addEventButton));

        // now we add to our list
        solo.enterText((EditText) solo.getView(R.id.event_title), testEvent);
        solo.enterText((EditText) solo.getView(R.id.event_comment), "Before midnight");
        // click add button
        solo.clickOnView(solo.getView(R.id.addEventButton));

        // assert that the entry is in the list
        solo.waitForText(testEvent);
        assertTrue(solo.searchText(testEvent));

        // cleanup db
        removeFromFireBase(testEvent, currentUsername);
    }

    /**
     * A method for cleaning up firebase additions
     * @param testEvent the event name to clean up
     * @param userName the user whose habit is associated with
     */
    private void removeFromFireBase(String testEvent, String userName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cr = db.collection("users").document(userName).collection("Events");
        cr.document(testEvent).delete();
    }
}
