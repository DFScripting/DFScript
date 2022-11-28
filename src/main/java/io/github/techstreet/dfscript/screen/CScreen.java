package io.github.techstreet.dfscript.screen;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.widget.CWidget;
import io.github.techstreet.dfscript.util.RenderUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class CScreen extends Screen {

    private final int width, height;
    public final List<CWidget> widgets = new ArrayList<>();

    protected CScreen(int width, int height) {
        super(Text.literal("DFScript Screen"));
        this.width = width;
        this.height = height;
        DFScript.MC.keyboard.setRepeatEvents(true);
    }

    @Override
    public void render(@NotNull MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
        renderBackground(stack);
        stack.push();
        MinecraftClient mc = DFScript.MC;

        stack.translate(mc.currentScreen.width/2f, mc.currentScreen.height/2f, 0);

//        float scaleFactor = (float) mc.getWindow().getScaleFactor();
        float scaleFactor = 2;
        stack.scale(scaleFactor,scaleFactor,0);

        stack.translate(-width/2f, -height/2f, 0);

        RenderUtil.renderGui(stack,0,0,width,height);

        mouseX += -mc.currentScreen.width/2;
        mouseY += -mc.currentScreen.height/2;

        mouseX /= scaleFactor;
        mouseY /= scaleFactor;

        mouseX += width/2;
        mouseY += height/2;

        for (CWidget cWidget : widgets) {
            cWidget.render(stack, mouseX, mouseY, tickDelta);
        }
        for (CWidget cWidget : widgets) {
            cWidget.renderOverlay(stack, mouseX, mouseY, tickDelta);
        }
        stack.pop();
        super.render(stack, mouseX, mouseY, tickDelta);
    }

    @Override
    public boolean charTyped(char ch, int keyCode) {
        for (CWidget cWidget : widgets) {
            cWidget.charTyped(ch, keyCode);
        }
        return super.charTyped(ch, keyCode);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (CWidget cWidget : widgets) {
            cWidget.keyPressed(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (CWidget cWidget : widgets) {
            cWidget.keyReleased(keyCode, scanCode, modifiers);
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseX = translateMouseX(mouseX);
        mouseY = translateMouseY(mouseY);

        for (int i = widgets.size() - 1; i >= 0; i--) {
            if (widgets.get(i).mouseClicked(mouseX, mouseY, button)) {
                break;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        mouseX = translateMouseX(mouseX);
        mouseY = translateMouseY(mouseY);

        for (CWidget cWidget : widgets) {
            cWidget.mouseScrolled(mouseX, mouseY, amount);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public double translateMouseX(double mouseX) {
        MinecraftClient mc = DFScript.MC;
//        float s = (float) mc.getWindow().getScaleFactor();
        float scaleFactor = 2;
        mouseX += -mc.currentScreen.width/2f;
        mouseX /= scaleFactor;
        mouseX += width/2f;
        return mouseX;
    }

    public double translateMouseY(double mouseY) {
        MinecraftClient mc = DFScript.MC;
//        float scaleFactor = (float) mc.getWindow().getScaleFactor();
        float scaleFactor = 2;
        mouseY += -mc.currentScreen.height/2f;
        mouseY /= scaleFactor;
        mouseY += height/2f;
        return mouseY;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        for(CWidget widget : widgets) {
            if(!widget.enableClosingOnEsc()) {
                return false;
            }
        }

        return super.shouldCloseOnEsc();
    }
}
