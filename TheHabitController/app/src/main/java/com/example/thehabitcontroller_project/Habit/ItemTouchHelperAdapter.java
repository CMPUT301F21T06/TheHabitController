package com.example.thehabitcontroller_project.Habit;

/**
 * The interface for implementing the custom item touch capabilities
 * for the {@link HabitRecyclerAdapter} so the Habit fragment can properly
 * handle the touch events
 */
public interface ItemTouchHelperAdapter {

    /**
     * Handles the movement of a certain entry in the RecyclerView's list so we can
     * move the entry into a different index in the list
     *
     * @param fromPosition  the index from which the entry is being moved from
     * @param toPosition    the index the entry is moved to
     */
    void onItemMove(int fromPosition, int toPosition);

    /**
     * Handles the swiping motion of a certain entry for deletion
     *
     * @param position
     */
    void onItemSwiped(int position);

    /**
     * Handles the event on which an entry in the list is dropped into a final position
     */
    void clearView();
}
