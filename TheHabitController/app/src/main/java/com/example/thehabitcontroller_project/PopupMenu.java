package com.example.thehabitcontroller_project;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thehabitcontroller_project.Community.User;
import com.example.thehabitcontroller_project.Community.UserArrayAdapter;
import com.example.thehabitcontroller_project.Community.UserSearchActivity;

import java.util.ArrayList;
import java.util.List;

public class PopupMenu {
    public void showPopupWindow(final View view, ArrayList<User> userList) {
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_menu, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        // Create window
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        // Set location
        popupWindow.showAtLocation(view, Gravity.TOP, 20, 0);

        RecyclerView rv = popupView.findViewById(R.id.rvFollowRq);
        LinearLayoutManager layoutManager=new LinearLayoutManager(view.getContext());
        rv.setLayoutManager(layoutManager);

        UserArrayAdapter.ClickListener clickListener = new UserArrayAdapter.ClickListener() {
            @Override
            public void onItemClick(int pos, User itemUser) {
                AlertDialog alertDialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Follow Request")
                        .setMessage("Follow request from user \""+itemUser.getUserName()+"\"?")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                User.acceptFollow(itemUser);
                                Toast.makeText(view.getContext(),
                                        "Accepted.", Toast.LENGTH_SHORT).show();
                                popupWindow.dismiss();
                            }
                        })
                        .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                User.requestFollow(itemUser);
                                Toast.makeText(view.getContext(),
                                        "Declined.", Toast.LENGTH_SHORT).show();
                                popupWindow.dismiss();
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        })
                        .create();
                alertDialog.show();
            }
        };

        UserArrayAdapter userArrayAdapter= new UserArrayAdapter(view.getContext(), userList, clickListener);
        rv.setAdapter(userArrayAdapter);
        userArrayAdapter.notifyDataSetChanged();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });
    }
}
