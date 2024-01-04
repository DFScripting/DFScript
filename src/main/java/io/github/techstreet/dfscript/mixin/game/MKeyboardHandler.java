package io.github.techstreet.dfscript.mixin.game;

import io.github.techstreet.dfscript.event.KeyPressEvent;
import io.github.techstreet.dfscript.event.system.EventManager;
import net.minecraft.client.Keyboard;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MKeyboardHandler {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void keyPress(long window, int i, int j, int k, int m, CallbackInfo ci) {
        Key key = InputUtil.fromKeyCode(i, j);

        KeyPressEvent event = new KeyPressEvent(key, k);
        EventManager.getInstance().dispatch(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

}