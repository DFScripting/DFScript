package io.github.techstreet.dfscript.event;

import io.github.techstreet.dfscript.event.system.CancellableEvent;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;

public class RecieveSoundEvent implements CancellableEvent {
    private boolean cancelled = false;
    private final PlaySoundS2CPacket packet;

    public RecieveSoundEvent(PlaySoundS2CPacket packet) {
        this.packet = packet;
    }

    public PlaySoundS2CPacket getPacket() {
        return packet;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
