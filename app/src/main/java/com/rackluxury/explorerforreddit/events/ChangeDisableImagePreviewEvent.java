package com.rackluxury.explorerforreddit.events;

public class ChangeDisableImagePreviewEvent {
    public boolean disableImagePreview;

    public ChangeDisableImagePreviewEvent(boolean disableImagePreview) {
        this.disableImagePreview = disableImagePreview;
    }
}
