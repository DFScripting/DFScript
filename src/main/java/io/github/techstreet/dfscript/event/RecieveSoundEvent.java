package io.github.techstreet.dfscript.event;

import io.github.techstreet.dfscript.event.system.CancellableEvent;
import net.minecraft.util.Identifier;

public class RecieveSoundEvent implements CancellableEvent {
    private boolean cancelled = false;

    private final Identifier soundId;
    private final float volume, pitch;

    public RecieveSoundEvent(Identifier soundId, float volume, float pitch) {
        this.soundId = soundId;
        this.volume = volume;
        this.pitch = pitch;
    }

    public Identifier getSoundId() {
        return soundId;
    }
    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
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
