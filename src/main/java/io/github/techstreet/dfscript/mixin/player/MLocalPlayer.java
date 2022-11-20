package io.github.techstreet.dfscript.mixin.player;

import io.github.techstreet.dfscript.event.SendChatEvent;
import io.github.techstreet.dfscript.event.TickEvent;
import io.github.techstreet.dfscript.event.system.EventManager;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
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

    @Inject(method = "sendCommand(Ljava/lang/String;)Z", at = @At("HEAD"), cancellable = true)
    private void command(String command, CallbackInfoReturnable<Boolean> ci) {
        ChatUtil.info("sendCommand: "+command);
        SendChatEvent event = new SendChatEvent("/"+command);
        EventManager.getInstance().dispatch(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "sendCommand(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void command2(String command, Text preview, CallbackInfo ci) {
        ChatUtil.info("sendCommandInternal: "+command);
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
