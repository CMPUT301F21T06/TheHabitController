package com.example.thehabitcontroller_project;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
        Event e = new Event("Slept early", c, l, "bitmapString", d);

        // make sure they are the same
        assertEquals(e.getTitle(),"Slept early");
        assertEquals(e.getComment(), c);
        assert e.getComment().length() <= 30; // max comment length is 20
        assertEquals(e.getDateEvent(), d);
        assertEquals(e.getLocation(), l);
        assertEquals(e.getBitmapString(), "bitmapString");
    }

    /**
     * Tests the simple date formatter of {@link Event} class to make sure it functions correctly
     */
    @Test
    public void TestFormattedDate() {
        // initialize our event
        Event e = new Event("Slept early", "Before midnight", "location", "bitmapString", new Date());

        // get the proper date format we want
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        String formattedDate = formatter.format(new Date());

        // make sure its the same as the Habit class provides
        assertEquals(e.getFormattedDate(), formattedDate);
    }
}