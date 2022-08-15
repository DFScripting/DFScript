package io.github.techstreet.dfscript.event.system;

public interface CancellableEvent extends Event {
    void setCancelled(boolean cancel);

    boolean isCancelled();
}
