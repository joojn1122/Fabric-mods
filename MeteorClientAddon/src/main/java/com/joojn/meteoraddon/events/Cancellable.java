package com.joojn.meteoraddon.events;

public class Cancellable {

    private boolean isCancelled = false;

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

}
