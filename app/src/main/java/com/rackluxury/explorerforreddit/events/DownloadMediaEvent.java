package com.rackluxury.explorerforreddit.events;

public class DownloadMediaEvent {
    public boolean isSuccessful;

    public DownloadMediaEvent(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }
}
