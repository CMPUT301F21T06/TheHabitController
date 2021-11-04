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

public class eventArrayAdapter extends ArrayAdapter<Event> {
    private List<Event> eventList;
    private Context context;

    public eventArrayAdapter(@NonNull Context context, @NonNull List<Event> eventList) {
        super(context, 0, eventList);
        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.event_content, parent, false);
        }
        TextView eventTitle = view.findViewById(R.id.event_name);
        eventTitle.setText(eventList.get(position).getTitle());
        return view;
    }
}