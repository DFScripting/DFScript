package io.github.techstreet.dfscript.mixin.sound;

import io.github.techstreet.dfscript.event.ReceiveSoundEvent;
import io.github.techstreet.dfscript.event.system.EventManager;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SoundSystem.class)
public abstract class MSoundSystem {

    @Shadow
    public abstract float getAdjustedVolume(SoundInstance sound);

    @Shadow
    public abstract float getAdjustedPitch(SoundInstance sound);

    @Shadow
    public abstract float getSoundVolume(SoundCategory category);

    @Inject(method = "play", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/sound/SoundSystem;getAdjustedPitch(Lnet/minecraft/client/sound/SoundInstance;)F"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void play(SoundInstance sound, CallbackInfo ci, WeightedSoundSet weightedSoundSet, Identifier identifier, Sound sound2, float f, float g, SoundCategory soundCategory, float h, float i) {
        ReceiveSoundEvent event = new ReceiveSoundEvent(sound.getId(), h, i);
        EventManager.getInstance().dispatch(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
