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

public class HabitRecyclerAdapter extends RecyclerView.Adapter<HabitRecyclerAdapter.HabitViewHolder> implements ItemTouchHelperAdapter {

    private List<Habit> habitList;
    private ItemTouchHelper itemTouchHelper;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();;
    private String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private OnHabitItemClickedListener onHabitItemClickedListener;
    private final DocumentReference userDr = db.collection("users").document(currentUser);
    private final CollectionReference usersCr = userDr.collection("Habits");

    public HabitRecyclerAdapter(List<Habit> habitList, OnHabitItemClickedListener onHabitItemClickedListener) {
        this.habitList = habitList;
        this.onHabitItemClickedListener = onHabitItemClickedListener;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        // move the item in our adapter
        Habit fromHabit = habitList.get(fromPosition);
        habitList.remove(fromPosition);

        habitList.add(toPosition, fromHabit);
        this.notifyItemMoved(fromPosition, toPosition);
    }


    // update the firebase with new ordering after user drops the ViewHolder in place
    @Override
    public void clearView() {
        Map<String, Object> docData = new HashMap<>();
        for (int i = 0; i < habitList.size(); i++) {
            docData.put("order", i);
            usersCr.document(habitList.get(i).getTitle()).set(docData, SetOptions.merge());
        }
    }

    @Override
    public void onItemSwiped(int position) {
        Habit h = habitList.get(position);
        usersCr.document(h.getTitle()).delete();
        habitList.remove(position);

        this.notifyItemRemoved(position);
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.itemTouchHelper = touchHelper;
    }

    public class HabitViewHolder extends RecyclerView.ViewHolder implements
            View.OnTouchListener, GestureDetector.OnGestureListener {
        private TextView habitTitle;
        private ProgressBar habitProgress;
        private GestureDetector gestureDetector;
        OnHabitItemClickedListener onItemListener;

        public HabitViewHolder(final View view, OnHabitItemClickedListener onItemListener) {
            super(view);
            habitTitle = (TextView) view.findViewById(R.id.habit_name);
            habitProgress = (ProgressBar) view.findViewById(R.id.habitProgressBar);
            gestureDetector = new GestureDetector(itemView.getContext(), this);

            itemView.setOnTouchListener(this);
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            onHabitItemClickedListener.onHabitClick(getAdapterPosition());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            itemTouchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return true;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        }
    }

    @NonNull
    @Override
    public HabitRecyclerAdapter.HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_content, parent, false);
        return new HabitViewHolder(itemView, onHabitItemClickedListener);
    }

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

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public interface OnHabitItemClickedListener{
        void onHabitClick(int position);
    }
}
