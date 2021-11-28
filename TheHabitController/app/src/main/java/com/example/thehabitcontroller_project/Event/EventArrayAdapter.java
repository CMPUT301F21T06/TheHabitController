package com.example.thehabitcontroller_project.Event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.thehabitcontroller_project.R;

import java.util.List;

/**
 * A custom adapter for {@link Event} objects that is a subclass of
 * {@link ArrayAdapter} for showing the Event's title in the event listview
 *
 * @author Tyler
 * @version 1.0.0
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {
    private List<Event> eventList;
    private Context context;

    /**
     * Constructor for the adapter, just stores the list of {@link Event} object and {@link Context}
     * @param context   The context of the fragment it came from as {@link Context}
     * @param eventList the {@link List} of {@link Event} objects
     */
    public EventArrayAdapter(@NonNull Context context, @NonNull List<Event> eventList) {
        super(context, 0, eventList);
        this.eventList = eventList;
        this.context = context;
    }

    /**
     * Override method for getView, which just inflates the layout we have and then eventArrayAdapter
     * handles what is shown on that layout so it appears on the actual list itself.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.event_content, parent, false);
        }
        // shows the title on the layout
        TextView eventTitle = view.findViewById(R.id.event_name);
        Event e = eventList.get(position);
        eventTitle.setText(e.getTitle());

        return view;
    }
}