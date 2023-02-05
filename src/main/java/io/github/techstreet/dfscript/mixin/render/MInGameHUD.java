package io.github.techstreet.dfscript.mixin.render;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.event.HudRenderEvent;
import io.github.techstreet.dfscript.event.system.EventManager;
import io.github.techstreet.dfscript.screen.overlay.OverlayManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
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

    @Inject(at = @At("HEAD"), method = "render")
    private void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        OverlayManager.getInstance().render(matrices);
        EventManager em = EventManager.getInstance();
        em.dispatch(new HudRenderEvent(matrices));
        em.resetEvents();
    }
}