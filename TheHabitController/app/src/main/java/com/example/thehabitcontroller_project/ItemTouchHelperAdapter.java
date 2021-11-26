package com.example.thehabitcontroller_project;

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemSwiped(int position);

    void clearView();
}
