package com.example.thehabitcontroller_project;

import android.app.Activity;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.idp.SingleSignInActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginTest {

    private Solo solo;

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        rule.getScenario().onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                solo = new Solo(InstrumentationRegistry.getInstrumentation(), activity);
            }
        });
    }

    /**
     * Test if sign in activity shows up correctly
     */
    @Test
    public void testLoginIntent() {
        solo.waitForActivity(SingleSignInActivity.class,2000);
    }

    /**
     * Simulate the whole login process
     */
    @Test
    public void testLoginUI() {
        solo.waitForActivity(SingleSignInActivity.class,2000);
        solo.enterText(0,"test@test.com");
        solo.clickOnButton(0);
        solo.waitForText("Welcome back!",1,1000);
        solo.enterText(0,"123456");
        solo.clickOnButton(0);
        solo.waitForActivity(MainActivity.class,2000);
        solo.assertCurrentActivity("Wrong Activity",MainActivity.class);
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


}