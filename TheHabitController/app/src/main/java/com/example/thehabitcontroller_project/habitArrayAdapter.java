package com.example.thehabitcontroller_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
public class habitArrayAdapter extends ArrayAdapter<Habit> {

    private List<Habit> habitList;
    private Context context;

    /**
     * Constructor for the adapter, just stores the list of {@link Habit} object and {@link Context}
     * @param context   The context of the fragment it came from as {@link Context}
     * @param habitList the {@link List} of {@link Habit} objects
     */
    public habitArrayAdapter(@NonNull Context context, @NonNull List<Habit> habitList) {
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
        habitTitle.setText(habitList.get(position).getTitle());
        return view;
    }
}
