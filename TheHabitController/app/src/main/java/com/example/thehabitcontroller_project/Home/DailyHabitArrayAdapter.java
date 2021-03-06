package com.example.thehabitcontroller_project.Home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.thehabitcontroller_project.Habit.Habit;
import com.example.thehabitcontroller_project.R;

import java.util.List;

/**
 * The custom adapter for showing Habit information in our Daily Habits fragment page
 */
public class DailyHabitArrayAdapter extends ArrayAdapter<Habit> {

    private List<Habit> dailyHabitList;
    private Context context;

    /**
     * Constructor for the Adapter
     *
     * @param context           The context
     * @param dailyHabitList    The {@link List} of {@link Habit}s
     */
    public DailyHabitArrayAdapter(@NonNull Context context, @NonNull List<Habit> dailyHabitList) {
        super(context, 0, dailyHabitList);
        this.dailyHabitList = dailyHabitList;
        this.context = context;
    }

    /**
     * Override for showing the Habit title and reason in our list
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        // inflate our layout for habits
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.daily_habits_content, parent, false);
        }

        TextView dailyHabitTitle = view.findViewById(R.id.daily_habit_name);
        dailyHabitTitle.setText(dailyHabitList.get(position).getTitle());
        TextView dailyHabitReason = view.findViewById(R.id.daily_habit_reason);
        dailyHabitReason.setText(dailyHabitList.get(position).getReason());

        return view;
    }
}
