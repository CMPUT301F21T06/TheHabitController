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

public class habitArrayAdapter extends ArrayAdapter<Habit> {
    private List<Habit> habitList;
    private Context context;

    public habitArrayAdapter(@NonNull Context context, @NonNull List<Habit> habitList) {
        super(context, 0, habitList);
        this.habitList = habitList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.habit_content, parent, false);
        }
        TextView habitTitle = view.findViewById(R.id.habit_name);
        habitTitle.setText(habitList.get(position).getTitle());
        return view;
    }
}
