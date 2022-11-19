package io.github.techstreet.dfscript.mixin.player;

import io.github.techstreet.dfscript.event.SendChatEvent;
import io.github.techstreet.dfscript.event.TickEvent;
import io.github.techstreet.dfscript.event.system.EventManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class MLocalPlayer {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void chat(String message, Text preview, CallbackInfo ci) {
        SendChatEvent event = new SendChatEvent(message);
        EventManager.getInstance().dispatch(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    private void command(String command, CallbackInfoReturnable<Boolean> ci) {
        SendChatEvent event = new SendChatEvent("/"+command);
        EventManager.getInstance().dispatch(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "sendCommandInternal", at = @At("HEAD"), cancellable = true)
    private void commandInterval(String command, @Nullable Text preview, CallbackInfo ci) {
        SendChatEvent event = new SendChatEvent("/"+command);
        EventManager.getInstance().dispatch(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        EventManager.getInstance().dispatch(new TickEvent());
    }

}
