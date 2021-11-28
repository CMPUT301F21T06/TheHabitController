package com.example.thehabitcontroller_project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserArrayAdapter extends RecyclerView.Adapter<UserArrayAdapter.ViewHolder> {

    interface ClickListener{
        public void onItemClick(int pos, User itemUser);
    }

    private Context context;
    private List<User> userList;
    private ClickListener clickListener;


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textUsername;
        private final TextView textEmail;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            textUsername = (TextView) view.findViewById(R.id.textUsername);
            textEmail = (TextView) view.findViewById(R.id.textEmail);
        }

        public TextView getTextUsername() {
            return textUsername;
        }

        public TextView getTextEmail() {
            return textEmail;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param userList String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public UserArrayAdapter(Context context,List<User> userList, ClickListener clickListener) {
        this.context=context;
        this.userList=userList;
        this.clickListener=clickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_content, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextUsername().setText(userList.get(position).getUserName());
        viewHolder.getTextEmail().setText(userList.get(position).getEmail());
        viewHolder.itemView.setOnClickListener(view ->
                clickListener.onItemClick(position, userList.get(position))
        );
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return userList.size();
    }
}