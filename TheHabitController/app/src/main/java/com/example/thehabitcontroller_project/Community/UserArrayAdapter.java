package com.example.thehabitcontroller_project.Community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.thehabitcontroller_project.R;

import java.util.List;

public class UserArrayAdapter extends RecyclerView.Adapter<UserArrayAdapter.ViewHolder> {

    interface ClickListener{
        public void onItemClick(int pos, User itemUser);
    }

    private Context context;
    private List<User> userList;
    private ClickListener clickListener;
    private static final int VIEW_TYPE_EMPTY=1;
    private static final int VIEW_TYPE_NORMAL=0;

    @Override
    public int getItemViewType(int position) {
        if (userList.isEmpty()){
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textUsername;
        private TextView textEmail;
        private TextView textEmptyMsg;

        public ViewHolder(View view, int viewType) {
            super(view);
            // Define click listener for the ViewHolder's View
            if (viewType==VIEW_TYPE_NORMAL){
                textUsername = (TextView) view.findViewById(R.id.textUsername);
                textEmail = (TextView) view.findViewById(R.id.textEmail);
            } else {
                textEmptyMsg = (TextView) view.findViewById(R.id.tvEmptyMsg);
            }
        }

        public TextView getTextUsername() {
            return textUsername;
        }

        public TextView getTextEmail() {
            return textEmail;
        }

        public TextView getTextEmptyMsg() {
            return textEmptyMsg;
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
        View view;
        switch (viewType){
            case VIEW_TYPE_EMPTY:
                view=LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.rv_empty, viewGroup, false);
                return new ViewHolder(view,VIEW_TYPE_EMPTY);
            default:
                view=LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.user_content, viewGroup, false);
                return new ViewHolder(view,VIEW_TYPE_NORMAL);
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (viewHolder.getItemViewType()==VIEW_TYPE_EMPTY){
            viewHolder.getTextEmptyMsg().setText("You are currently following nobody.");
        }else{
            viewHolder.getTextUsername().setText(userList.get(position).getUserName());
            viewHolder.getTextEmail().setText(userList.get(position).getEmail());
            viewHolder.itemView.setOnClickListener(view ->
                    clickListener.onItemClick(position, userList.get(position))
            );
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return userList.size();
    }
}