package io.github.techstreet.dfscript.event;

import io.github.techstreet.dfscript.event.system.CancellableEvent;
import net.kyori.adventure.text.Component;
import net.minecraft.text.Text;

public class ReceiveChatEvent implements CancellableEvent {
    private final Component message;
    private boolean cancelled = false;

    public ReceiveChatEvent(Component message) {
        this.message = message;
    }

    public Component getMessage() {
        return message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
