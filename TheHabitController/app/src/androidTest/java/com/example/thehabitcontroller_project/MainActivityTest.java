package com.example.thehabitcontroller_project;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.app.Activity;
import android.widget.EditText;

import com.firebase.ui.auth.ui.idp.SingleSignInActivity;
import com.robotium.solo.Solo;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * The UI testing of All Fragment classes done with {@link Solo} from Robotium
 */
public class MainActivityTest {
    private Solo solo;
    private final String currentUsername = "currentUser";
    private final String userName = "DONOTDELETE@test.ca";
    private final String pass = "testtest1";

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }


    /**
     * Initialize our {@link Solo} object for intent testing
     * Also handles logging in past the initial login screen
     * @throws Exception
     */
    @Before
    public void setUpLogin() throws Exception {
        // navigate through email entering if prompted
        if (solo.searchText("Sign in")) {
            solo.enterText((EditText) solo.getView(R.id.email), userName);
            solo.clickOnView(solo.getView(R.id.button_next));

            // enter the password
            solo.enterText((EditText) solo.getView(R.id.password), pass);
            solo.clickOnButton(0);
        }
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void MainTestActivity() throws InterruptedException {
        // first we are in Home Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        // click on the habits menu UI
        solo.clickOnView(solo.getView(R.id.habits));
        // wait for fragment and get our habit list on the habit fragment activity
        solo.waitForFragmentById(R.id.habits, 2000);
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.habit_list));

        // click on the events menu UI
        solo.clickOnView(solo.getView(R.id.events));
        // wait for fragment and get our event list on the event fragment activity
        solo.waitForFragmentById(R.id.events, 2000);
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.event_list));

        // click on the community menu UI
        solo.clickOnView(solo.getView(R.id.community));
        // wait for fragment and get our community list on the community fragment activity
        solo.waitForFragmentById(R.id.community, 2000);




    }

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}

