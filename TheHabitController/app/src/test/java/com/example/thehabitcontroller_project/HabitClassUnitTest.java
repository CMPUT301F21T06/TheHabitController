package com.example.thehabitcontroller_project;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Unit test file for testing the {@link Habit} class
 */
public class HabitClassUnitTest {

    /**
     * This tests the constructor of the {@link Habit} class and makes sure that
     * its setters are working and returning the correct values we initialized it with
     */
    @Test
    public void TestHabitConstructor() {
        // initialize our Habit and Date
        Date d = new Date();
        Habit h = new Habit("Running", "Want to get better", d, true);

        // make sure they are the same
        assertEquals(h.getTitle(),"Running");
        assertEquals(h.getReason(),"Want to get better");
        assertEquals(h.getDateStart(), d);
        assertEquals(h.isPublic(), true);
    }

    /**
     * Tests using the setters of {@link Habit} to change its values
     */
    @Test
    public void TestChangingHabit() {
        // initialize a habit with initial values
        Habit h = new Habit("Running", "Want to get better", new Date(), true);

        // change all values
        h.setTitle("Sprinting");
        h.setReason("Want to do 100m soon");
        Date d = new GregorianCalendar(2021, Calendar.NOVEMBER, 22).getTime();
        h.setDateStart(d);
        h.setPublic(false);

        // test they are the same as the changed values
        assertEquals(h.getTitle(),"Sprinting");
        assertEquals(h.getReason(),"Want to do 100m soon");
        assertEquals(h.getDateStart(), d);
        assertEquals(h.isPublic(), false);
    }

    /**
     * Tests the simple date formatter of {@link Habit} class to make sure it functions correctly
     */
    @Test
    public void TestFormattedDate() {
        // initialize our habit
        Habit h = new Habit("Running", "Want to get better", new Date(), true);

        // get the proper date format we want
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        String formattedDate = formatter.format(new Date());

        // make sure its the same as the Habit class provides
        assertEquals(h.getFormattedDate(), formattedDate);
    }
}