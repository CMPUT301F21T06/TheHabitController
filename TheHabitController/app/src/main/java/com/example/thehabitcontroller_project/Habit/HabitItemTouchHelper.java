package com.example.thehabitcontroller_project.Habit;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A helper that overrides specific Callback methods of ItemTouchHelper
 * so we can have custom features in our app like swipe for deletion, and drag+drop
 */
public class HabitItemTouchHelper extends ItemTouchHelper.Callback{

    private final ItemTouchHelperAdapter adapter;

    /**
     * Constructor for our custom item touch helper
     *
     * @param adapter the ItemTouchHelperAdapter interface telling us which methods to custom-implement
     */
    public HabitItemTouchHelper(ItemTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Override that handles what the colour of the item being dragged after it gets dropped
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(Color.WHITE);
        adapter.clearView();
    }

    /**
     * Override that handles what colour the item turns into when its selected and dragged is finished
     */
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
        }
    }

    /**
     * Enable swiping
     * @return true
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /**
     * Disable long press dragging
     * @return false
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    /**
     * Overriding the method where we set the movements allowed in our RecyclerView
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * Overriding the method for handling on-move events when an entry is dragged wtihin the RecyclerView
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    /**
     * Override the method for handling deletion of a Habit when swipped left and right
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemSwiped(viewHolder.getAdapterPosition());
    }

}
