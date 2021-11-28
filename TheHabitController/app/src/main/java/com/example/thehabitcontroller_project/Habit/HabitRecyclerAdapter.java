package com.example.thehabitcontroller_project.Habit;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thehabitcontroller_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Our RecyclerAdapter that will also handle the touch-events for single item selection,
 * drag for deletion, as well as drag and drop for moving {@link Habit}s within the {@link RecyclerView} list
 */
public class HabitRecyclerAdapter extends RecyclerView.Adapter<HabitRecyclerAdapter.HabitViewHolder> implements ItemTouchHelperAdapter {

    private List<Habit> habitList;
    private ItemTouchHelper itemTouchHelper;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();;
    private String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private OnHabitItemClickedListener onHabitItemClickedListener;
    private final DocumentReference userDr = db.collection("users").document(currentUser);
    private final CollectionReference usersCr = userDr.collection("Habits");

    /**
     * Constructor for creating our Adapter
     *
     * @param habitList                     The list of {@link Habit}s
     * @param onHabitItemClickedListener    The {@link OnHabitItemClickedListener} the listener for click events on the adapter
     */
    public HabitRecyclerAdapter(List<Habit> habitList, OnHabitItemClickedListener onHabitItemClickedListener) {
        this.habitList = habitList;
        this.onHabitItemClickedListener = onHabitItemClickedListener;
    }

    /**
     * The Override where we handle the moving of one {@link Habit} in a position to another
     *
     * @param fromPosition  the index from which the entry is being moved from
     * @param toPosition    the index the entry is moved to
     */
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        // move the item in our adapter
        Habit fromHabit = habitList.get(fromPosition);
        habitList.remove(fromPosition);

        // add the habit to the position and render the changes for this adapter
        habitList.add(toPosition, fromHabit);
        this.notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * Override for handling the dropping of the {@link Habit} into its final position
     * within the {@link RecyclerView} list, where we update the Firestore database
     */
    @Override
    public void clearView() {
        // go through the list and update the order for the new ordering
        Map<String, Object> docData = new HashMap<>();
        for (int i = 0; i < habitList.size(); i++) {
            docData.put("order", i);
            usersCr.document(habitList.get(i).getTitle()).set(docData, SetOptions.merge());
        }
    }

    /**
     * Override for handling the swip-event, in this case we will delete the {@link Habit}
     *
     * @param position the index of the {@link Habit} that we are deleting
     */
    @Override
    public void onItemSwiped(int position) {
        // delete our habit from our list and the database
        Habit h = habitList.get(position);
        usersCr.document(h.getTitle()).delete();
        habitList.remove(position);

        // render the deletion change
        this.notifyItemRemoved(position);
    }

    /**
     * Setter for our custom item touch helper
     * @param touchHelper the {@link ItemTouchHelper} of the {@link HabitRecyclerAdapter}
     */
    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.itemTouchHelper = touchHelper;
    }

    /**
     * The ViewHolder class to hold the actual entries in our {@link RecyclerView} list
     */
    public class HabitViewHolder extends RecyclerView.ViewHolder implements
            View.OnTouchListener, GestureDetector.OnGestureListener {

        private TextView habitTitle;
        private ProgressBar habitProgress;
        private GestureDetector gestureDetector;
        OnHabitItemClickedListener onItemListener;

        /**
         * Constructor for our custom ViewHolder for {@link Habit}s
         *
         * @param view              The {@link View} containing the place where we display our {@link Habit} info
         * @param onItemListener    The listener for handling item-clicks inside the {@link RecyclerView}
         */
        public HabitViewHolder(final View view, OnHabitItemClickedListener onItemListener) {
            super(view);
            // get our title and progress bar that shows in our Habit's list
            habitTitle = (TextView) view.findViewById(R.id.habit_name);
            habitProgress = (ProgressBar) view.findViewById(R.id.habitProgressBar);
            gestureDetector = new GestureDetector(itemView.getContext(), this);
            // set the on-touch listener to our custom one
            itemView.setOnTouchListener(this);
        }

        /**
         * Override required for implementation of OnTouchListener for OnDown events
         * but is not implemented for our app
         */
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        /**
         * Override required for implementation of OnTouchListener for OnShowPress events
         * but is not implemented for our app
         */
        @Override
        public void onShowPress(MotionEvent motionEvent) {
        }

        /**
         * For handling the single-item selection in our {@link RecyclerView} list
         */
        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            // get the adapter position (current item) and send it back to the fragment
            onHabitItemClickedListener.onHabitClick(getAdapterPosition());
            return true;
        }

        /**
         * Override required for implementation of OnTouchListener for OnDown events
         * but is not implemented for our app
         */
        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        /**
         * Override required for implementation of OnTouchListener for OnDown events
         * but is not implemented for our app
         */
        @Override
        public void onLongPress(MotionEvent motionEvent) {
            itemTouchHelper.startDrag(this);
        }

        /**
         * Override required for implementation of OnTouchListener for OnFling events
         * but is not implemented for our app
         */
        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return true;
        }

        /**
         * Override for handling the onTouch functionality for our {@link RecyclerView} where
         * we register the onTouchEvent for the {@link GestureDetector}
         */
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        }
    }

    /**
     * Override for our custom ViewHolder, where we give the view we're inflating with our
     * {@link Habit} information to the ViewHolder to render item in the {@link RecyclerView}
     */
    @NonNull
    @Override
    public HabitRecyclerAdapter.HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_content, parent, false);
        return new HabitViewHolder(itemView, onHabitItemClickedListener);
    }

    /**
     * Override for displaying the {@link Habit} information to our view
     *
     * @param holder    Our custom {@link HabitViewHolder} for {@link Habit}s
     * @param position  the position of the Habit in our adapter
     */
    @Override
    public void onBindViewHolder(@NonNull HabitRecyclerAdapter.HabitViewHolder holder, int position) {
        Habit h = habitList.get(position);
        holder.habitTitle.setText(h.getTitle());

        // get error-check the totalShownTimes
        if (h.getTotalShownTimes() == 0) {
            h.setTotalShownTimes(1);
        }

        // set the progress bar percentage and colour depending on completion percentage
        int progressPercent = h.getPercentageCompleted();
        holder.habitProgress.setProgress(progressPercent);
        if (progressPercent < 30) {
            holder.habitProgress.setProgressTintList(ColorStateList.valueOf(Color.RED));
        }
        else if (30 <= progressPercent && progressPercent < 70) {
            holder.habitProgress.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        }
        else {
            holder.habitProgress.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }
    }

    /**
     * gets the number of items we're currently holding
     * @return the number of items in the adapter as an int
     */
    @Override
    public int getItemCount() {
        return habitList.size();
    }

    /**
     * Override for getting a specific item at a position in the adapter
     *
     * @param position the index of the item in the list
     * @return the {@link Habit} at the index we're given
     */
    public Habit getItem(int position) {
        return this.habitList.get(position);
    }

    /**
     * Interface for implementing the method required for our onclickedlistener for handling
     * touch-events on a specific habit in the list
     */
    public interface OnHabitItemClickedListener{
        void onHabitClick(int position);
    }
}
