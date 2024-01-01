package io.github.techstreet.dfscript.mixin.render;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.script.ScriptListScreen;
import io.github.techstreet.dfscript.util.render.BlendableTexturedButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class MOptionsScreen extends Screen {

    @Unique
    private final Identifier identifier_main = new Identifier(DFScript.MOD_ID + ":scripts");
    @Unique
    private final Identifier identifier_main_highlight = new Identifier(DFScript.MOD_ID + "dfscript:scripts_highlight");

    public MOptionsScreen(Text literalText) {
        super(literalText);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    protected void init(CallbackInfo callbackInfo) {
        this.addDrawableChild(new BlendableTexturedButtonWidget(5, 5, 20, 20, identifier_main, identifier_main_highlight, (button) -> {
            ScriptListScreen screen = new ScriptListScreen(DFScript.MC.player != null);
            DFScript.MC.setScreen(screen);
        }));
    }
}