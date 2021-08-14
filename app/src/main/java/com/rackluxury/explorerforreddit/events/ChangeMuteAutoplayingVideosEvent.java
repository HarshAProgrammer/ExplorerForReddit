package com.rackluxury.explorerforreddit.events;

public class ChangeMuteAutoplayingVideosEvent {
    public boolean muteAutoplayingVideos;

    public ChangeMuteAutoplayingVideosEvent(boolean muteAutoplayingVideos) {
        this.muteAutoplayingVideos = muteAutoplayingVideos;
    }
}
