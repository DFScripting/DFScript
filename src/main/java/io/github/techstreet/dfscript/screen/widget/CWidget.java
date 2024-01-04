package io.github.techstreet.dfscript.screen.widget;

import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public interface CWidget {

    void render(DrawContext context, int mouseX, int mouseY, float tickDelta);

    default boolean mouseClicked(double x, double y, int button) {
        return false;
    }

    default void charTyped(char ch, int keyCode) {
    }

    default void keyPressed(int keyCode, int scanCode, int modifiers) {
    }

    default void keyReleased(int keyCode, int scanCode, int modifiers) {
    }

    default void mouseScrolled(double mouseX, double mouseY, double vertical, double horizontal) {
    }

    default void renderOverlay(DrawContext context, int mouseX, int mouseY, float tickDelta) {

    }

    Rectangle getBounds();

    default boolean enableClosingOnEsc() {
        return true;
    }
}
