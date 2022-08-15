package io.github.techstreet.dfscript.mixin.render;

import io.github.techstreet.dfscript.DFScript;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MInGameHUD {

    @Inject(at = @At("HEAD"), method = "renderScoreboardSidebar", cancellable = true)
    private void renderScoreboardSidebar(CallbackInfo info) {
        MinecraftClient client = DFScript.MC;
        if (client.options.debugEnabled) {
            info.cancel();
        }
    }
}