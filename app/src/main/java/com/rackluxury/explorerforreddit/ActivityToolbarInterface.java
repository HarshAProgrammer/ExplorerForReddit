package com.rackluxury.explorerforreddit;

public interface ActivityToolbarInterface {
    void onLongPress();
    default void displaySortType() {}
}
