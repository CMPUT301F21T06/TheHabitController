package com.example.thehabitcontroller_project;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        List<Boolean> schedule = new ArrayList<Boolean>(Arrays.asList(new Boolean[7]));
        Collections.fill(schedule, Boolean.FALSE);
        Habit h = new Habit("Running", "Want to get better", d, true, schedule);

        // make sure they are the same
        assertEquals(h.getTitle(),"Running");
        assertEquals(h.getReason(),"Want to get better");
        assertEquals(h.getDateStart(), d);
        assertEquals(h.isPublic(), true);
        assertEquals(h.getSchedule(), schedule);
    }

    /**
     * Tests using the setters of {@link Habit} to change its values
     */
    @Test
    public void TestChangingHabit() {
        // initialize a habit with initial values
        List<Boolean> schedule = new ArrayList<Boolean>(Arrays.asList(new Boolean[7]));
        Collections.fill(schedule, Boolean.FALSE);
        Habit h = new Habit("Running", "Want to get better", new Date(), true, schedule);

        // change all values
        h.setTitle("Sprinting");
        h.setReason("Want to do 100m soon");
        Date d = new GregorianCalendar(2021, Calendar.NOVEMBER, 22).getTime();
        h.setDateStart(d);
        h.setPublic(false);
        List<Boolean> schedule2 = new ArrayList<Boolean>(Arrays.asList(new Boolean[7]));
        Collections.fill(schedule, Boolean.TRUE);
        h.setSchedule(schedule2);

        // test they are the same as the changed values
        assertEquals(h.getTitle(),"Sprinting");
        assertEquals(h.getReason(),"Want to do 100m soon");
        assertEquals(h.getDateStart(), d);
        assertEquals(h.isPublic(), false);
        assertEquals(h.getSchedule(), schedule2);
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

    /**
     * Tests the internal calculator for seeing how many times this Habit has been done
     * Also tests for the extreme cases of percentage > 100 and < 0
     */
    @Test
    public void TestPercentageDoneCalculation() {
        // initialize our times done, total, and the actual percentage
        int timesDone = 10;
        int timesTotal = 100;
        int percentageDone = 10;

        // initialize our habit
        List<Boolean> schedule = new ArrayList<Boolean>(Arrays.asList(new Boolean[7]));
        Collections.fill(schedule, Boolean.FALSE);
        Habit h = new Habit("Running", "Want to get better", new Date(), true, schedule, timesDone, timesTotal);

        assertEquals(percentageDone, h.getPercentageCompleted());

        // test for div by 0
        h.setTimesFinished(0);
        h.setTotalShownTimes(0);

        assertEquals(0, h.getPercentageCompleted());

        // test for <0 (should be corrected)
        h.setTimesFinished(-1);
        h.setTotalShownTimes(10);

        assertEquals(0, h.getPercentageCompleted());

        // test for >100 percentage
        h.setTimesFinished(10);
        h.setTotalShownTimes(1);

        assertEquals(100, h.getPercentageCompleted());
    }
}