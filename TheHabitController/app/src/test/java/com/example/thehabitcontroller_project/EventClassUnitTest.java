package com.example.thehabitcontroller_project;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.thehabitcontroller_project.Event.Event;
import com.example.thehabitcontroller_project.Habit.Habit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Unit test file for testing the {@link Event} class
 */
public class EventClassUnitTest {

    /**
     * This tests the constructor of the {@link Event} class and makes sure that
     * its getters are working and returning the correct values we initialized it with
     */
    @Test
    public void TestEventConstructor() {
        // initialize our Habit and Event
        Date d = new Date();
        List<Boolean> schedule = new ArrayList<Boolean>(Arrays.asList(new Boolean[7]));
        Collections.fill(schedule, Boolean.FALSE);
        Habit h = new Habit("Running", "Want to get faster", d, true, schedule);
        Event e = new Event("Morning Run", "Woke early", d, "location", "image.png", "Running");

        // checks if the getters return the correct values
        assertEquals(e.getTitle(),"Morning Run");
        assertEquals(e.getComment(), "Woke early");
        assert e.getComment().length() <= 20; // max comment length is 20
        assertEquals(e.getDateEvent(), d);
        assertEquals(e.getLocation(), "location");
        assertEquals(e.getPhotoString(), "image.png");
        assertEquals(e.getHabitTitle(),"Running");
    }

    /**
     * Tests using the setters of {@link Event} to change its values
     */
    @Test
    public void TestChangingEvent() {
        // initialize our Habit and Event
        Date d = new Date();
        List<Boolean> schedule = new ArrayList<Boolean>(Arrays.asList(new Boolean[7]));
        Collections.fill(schedule, Boolean.FALSE);
        Habit h = new Habit("Running", "Want to get faster", d, true, schedule);
        Event e = new Event("Morning Run", "Woke early", d, "location", "image.png", "Running");

        String comment = "Hiked at 9am in the Wolf Willow Ravine";
        int maxLength = 20; // limit comment length to 20
        comment = comment.substring(0, maxLength);

        // use the setters to change the event values
        e.setTitle("Morning Hike");
        e.setComment(comment);
        Date d2 = new Date();
        e.setDateEvent(d2);
        e.setLocation("Wolf Willow");
        e.setPhotoString("ravine.png");
        e.setHabitTitle("Hike More");

        // checks if the setters changed the values correctly
        assertEquals(e.getTitle(),"Morning Hike");
        assertEquals(e.getComment(), comment);
        assert e.getComment().length() <= 20; // max comment length is 20
        assertEquals(e.getDateEvent(), d2);
        assertEquals(e.getLocation(), "Wolf Willow");
        assertEquals(e.getPhotoString(), "ravine.png");
        assertEquals(e.getHabitTitle(),"Hike More");
    }

    /**
     * Tests the simple date formatter of {@link Event} class to make sure it functions correctly
     */
    @Test
    public void TestFormattedDate() {
        // initialize a habit
        List<Boolean> schedule = new ArrayList<Boolean>(Arrays.asList(new Boolean[7]));
        Collections.fill(schedule, Boolean.FALSE);
        Habit h = new Habit("Running", "Want to get faster", new Date(), true, schedule);

        // initialize our event
        Event e = new Event("Morning Run", "Woke early", new Date(), "location", "image.png","Running");

        // get the proper date format we want
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        String formattedDate = formatter.format(new Date());

        // make sure its the same as the Habit class provides
        assertEquals(e.getFormattedDate(), formattedDate);
    }
}