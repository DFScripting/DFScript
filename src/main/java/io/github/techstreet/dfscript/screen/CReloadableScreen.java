package io.github.techstreet.dfscript.screen;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.widget.CWidget;
import io.github.techstreet.dfscript.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class CReloadableScreen extends CScreen {

    public abstract void reload();

    protected CReloadableScreen(int width, int height) {
        super(width, height);
    }
}
