package com.example.thehabitcontroller_project;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.thehabitcontroller_project.Event.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
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
        String c = "Slept before midnight";
        String l = "location";
//        Location l = new Location("Home");
//        l.setLongitude(0);
//        l.setLatitude(0);
//        Bitmap b = Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888);
        Event e = new Event("Slept early", c, d, l, "photoString");

        // make sure they are the same
        assertEquals(e.getTitle(),"Slept early");
        assertEquals(e.getComment(), c);
        assert e.getComment().length() <= 30; // max comment length is 20
        assertEquals(e.getDateEvent(), d);
        assertEquals(e.getLocation(), l);
        assertEquals(e.getPhotoString(), "photoString");
    }

    /**
     * Tests the simple date formatter of {@link Event} class to make sure it functions correctly
     */
    @Test
    public void TestFormattedDate() {
//        Bitmap b = Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888);
        // initialize our event
        Event e = new Event("Slept early", "Before midnight", new Date(), "location", "photoString");

        // get the proper date format we want
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        String formattedDate = formatter.format(new Date());

        // make sure its the same as the Habit class provides
        assertEquals(e.getFormattedDate(), formattedDate);
    }
}