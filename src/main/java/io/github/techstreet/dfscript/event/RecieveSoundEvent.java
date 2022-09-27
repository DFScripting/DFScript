package io.github.techstreet.dfscript.event;

import io.github.techstreet.dfscript.event.system.CancellableEvent;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class RecieveSoundEvent implements CancellableEvent {
    private boolean cancelled = false;

    private final SoundInstance sound;

    public RecieveSoundEvent(SoundInstance sound) {
        this.sound = sound;
    }

    public Identifier getSoundId() {
        return sound.getId();
    }
    public float getVolume() {
        return sound.getVolume();
    }

    public float getPitch() {
        return sound.getPitch();
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
