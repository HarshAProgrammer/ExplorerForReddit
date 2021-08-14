package com.rackluxury.explorerforreddit.events;

public class ChangeHidePostTypeEvent {
    public boolean hidePostType;

    public ChangeHidePostTypeEvent(boolean hidePostType) {
        this.hidePostType = hidePostType;
    }
}
