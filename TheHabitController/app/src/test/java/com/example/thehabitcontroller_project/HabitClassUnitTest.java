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
        Date d = new Date();
        Habit h = new Habit("Running", "Want to get better", d, true);

        assertEquals(h.getTitle(),"Running");
        assertEquals(h.getReason(),"Want to get better");
        assertEquals(h.getDateStart(), d);
        assertEquals(h.isPublic(), true);
    }

    /**
     *
     */
    @Test
    public void TestChangingHabit() {
        Habit h = new Habit("Running", "Want to get better", new Date(), true);

        h.setTitle("Sprinting");
        h.setReason("Want to do 100m soon");
        Date d = new GregorianCalendar(2021, Calendar.NOVEMBER, 22).getTime();
        h.setDateStart(d);
        h.setPublic(false);

        assertEquals(h.getTitle(),"Sprinting");
        assertEquals(h.getReason(),"Want to do 100m soon");
        assertEquals(h.getDateStart(), d);
        assertEquals(h.isPublic(), false);
    }

    @Test
    public void TestFormattedDate() {
        Habit h = new Habit("Running", "Want to get better", new Date(), true);

        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        String formattedDate = formatter.format(new Date());

        assertEquals(h.getFormattedDate(), formattedDate);
    }
}