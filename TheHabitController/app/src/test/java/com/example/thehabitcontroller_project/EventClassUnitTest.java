package com.example.thehabitcontroller_project;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.thehabitcontroller_project.Event.Event;
import com.example.thehabitcontroller_project.Habit.Habit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Unit test file for testing the {@link Event} class
 */
public class EventClassUnitTest {

    /**
     * This tests the constructor of the {@link Event} class and makes sure that
     * its setters are working and returning the correct values we initialized it with
     */
    @Test
    public void TestEventConstructor() {
        // initialize our Habit and Date
        Date d = new Date();
        String c = "Woke early";
        String l = "location";
//        Location l = new Location("Home");
//        l.setLongitude(0);
//        l.setLatitude(0);
//        Bitmap b = Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888);
        Event e = new Event("Morning Run", c, d, l, "photoString", "Running");

        // make sure they are the same
        assertEquals(e.getTitle(),"Woke early");
        assertEquals(e.getComment(), c);
        assert e.getComment().length() <= 30; // max comment length is 20
        assertEquals(e.getDateEvent(), d);
        assertEquals(e.getLocation(), l);
        assertEquals(e.getPhotoString(), "photoString");
        assertEquals(e.getHabitTitle(),"Running");
    }

    /**
     * Tests the simple date formatter of {@link Event} class to make sure it functions correctly
     */
    @Test
    public void TestFormattedDate() {
        // initialize a habit
        List<Boolean> schedule = new ArrayList<Boolean>(Arrays.asList(new Boolean[7]));
        Collections.fill(schedule, Boolean.FALSE);
        Habit h = new Habit("Running", "Want to get better", new Date(), true, schedule);

//        Bitmap b = Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888);
        // initialize our event
        Event e = new Event("Morning Run", "Woke early", new Date(), "location", "photoString","Running");

        // get the proper date format we want
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        String formattedDate = formatter.format(new Date());

        // make sure its the same as the Habit class provides
        assertEquals(e.getFormattedDate(), formattedDate);
    }
}