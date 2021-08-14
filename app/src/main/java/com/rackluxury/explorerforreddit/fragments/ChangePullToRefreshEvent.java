package com.rackluxury.explorerforreddit.fragments;

public class ChangePullToRefreshEvent {
    public boolean pullToRefresh;

    public ChangePullToRefreshEvent(boolean pullToRefresh) {
        this.pullToRefresh = pullToRefresh;
    }
}
