package com.example.thehabitcontroller_project;

import static org.junit.Assert.assertNotNull;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.thehabitcontroller_project.Community.CommunityFragmentActivity;
import com.example.thehabitcontroller_project.Community.UserSearchActivity;
import com.example.thehabitcontroller_project.Home.HomeFragmentActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class CommunityTest {
    private Solo solo;
    private final String email="mark@gmail.com";
    private final String password="asdf1234";

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
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        if (solo.searchText("E-mail")) {
            solo.enterText((EditText) solo.getView(R.id.tiLoginEmailTextField), email);
            solo.enterText((EditText) solo.getView(R.id.tiLoginPasswordTextField), password);
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

    @Test
    public void CommunityFragmentTest(){
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);
        Assert.assertNull(solo.getCurrentActivity().findViewById(R.id.rvFollowing));
        solo.clickOnView(solo.getView(R.id.community));
        solo.waitForText("Community",2,2000);
        assertNotNull(solo.getCurrentActivity().findViewById(R.id.rvFollowing));
    }

    @Test
    public void UserSearchTest() {
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.community));
        solo.waitForText("Community",2,2000);
        // click on fab
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.fabSearch));
        // see if it shows correct activity
        solo.assertCurrentActivity("Wrong activity", UserSearchActivity.class);
        solo.enterText(0,"Mike");
        solo.pressSoftKeyboardSearchButton();
        solo.waitForText("Mike",1,3000);
        solo.clickInRecyclerView(0);
        solo.waitForDialogToOpen();
        solo.clickOnButton("No");
        solo.clickInRecyclerView(0);
        solo.waitForDialogToOpen();
        solo.clickOnButton("Yes");
        solo.waitForText("Request sent",1,3000);
    }
}
