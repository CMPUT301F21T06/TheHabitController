package com.example.thehabitcontroller_project;

import android.util.Log;
import android.widget.TextView;
import android.view.View;

public class NotificationCounter {

    private TextView notificationNumber;
    private final int MAX = 99;
    private int notification_Number_counter = 1;

    public NotificationCounter(View view){
        notificationNumber = view.findViewById(R.id.notificationNumber);
    }

    public void increaseNumber()
    {
        notification_Number_counter++;

        if(notification_Number_counter > MAX)
        {
            Log.d("Counter","Max Notification");
        }
        else
        {
            notificationNumber.setText(String.valueOf(notification_Number_counter));
        }
    }

}
