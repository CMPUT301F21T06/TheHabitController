package com.example.thehabitcontroller_project;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A custom adapter for {@link Habit} objects that is a subclass of
 * {@link ArrayAdapter} for showing the Habit's title in the habit listview
 *
 * @author Steven
 * @version 1.0.0
 */
public class HabitArrayAdapter extends ArrayAdapter<Habit> {

    private List<Habit> habitList;
    private Context context;

    /**
     * Constructor for the adapter, just stores the list of {@link Habit} object and {@link Context}
     * @param context   The context of the fragment it came from as {@link Context}
     * @param habitList the {@link List} of {@link Habit} objects
     */
    public HabitArrayAdapter(@NonNull Context context, @NonNull List<Habit> habitList) {
        super(context, 0, habitList);
        this.habitList = habitList;
        this.context = context;
    }

    /**
     * Override method for getView, which just inflates the layout we have and then habitArrayAdapter
     * handles what is shown on that layout so it appears on the actual list itself.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        // inflate our layout for habits
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.habit_content, parent, false);
        }
        // set the title to be shown on the layout
        TextView habitTitle = view.findViewById(R.id.habit_name);
        ProgressBar habitProgress = view.findViewById(R.id.habitProgressBar);
        Habit h = habitList.get(position);
        habitTitle.setText(h.getTitle());
        if (h.getTotalShownTimes() == 0) {
            h.setTotalShownTimes(1);
        }
        int progressPercent = h.getPercentageCompleted();
        habitProgress.setProgress(progressPercent);
        if (progressPercent < 30) {
            habitProgress.setProgressTintList(ColorStateList.valueOf(Color.RED));
        }
        else if (30 >= progressPercent && progressPercent > 70) {
            habitProgress.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        }
        else {
            habitProgress.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }

        return view;
    }
}
