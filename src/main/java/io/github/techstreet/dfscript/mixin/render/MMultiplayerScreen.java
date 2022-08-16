package io.github.techstreet.dfscript.mixin.render;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.script.ScriptListScreen;
import io.github.techstreet.dfscript.util.render.BlendableTexturedButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MMultiplayerScreen extends Screen {
    private final Identifier identifier_main = new Identifier(DFScript.MOD_ID + ":scripts.png");

    protected MMultiplayerScreen(Text title) {
        super(title);
    }

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.addDrawableChild(new BlendableTexturedButtonWidget(5, 5, 20, 20, 0, 0, 20, identifier_main, 20, 40, (button) -> {
                    DFScript.MC.setScreen(new ScriptListScreen());
                }));
    }
}
