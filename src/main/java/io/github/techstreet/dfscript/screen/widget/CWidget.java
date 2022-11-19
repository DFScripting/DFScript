package io.github.techstreet.dfscript.screen.widget;

import java.awt.Rectangle;
import net.minecraft.client.util.math.MatrixStack;

public interface CWidget {

    void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta);

    default boolean mouseClicked(double x, double y, int button) {
        return false;
    }

    default void charTyped(char ch, int keyCode) {
    }

    default void keyPressed(int keyCode, int scanCode, int modifiers) {
    }

    default void keyReleased(int keyCode, int scanCode, int modifiers) {
    }

    default void mouseScrolled(double mouseX, double mouseY, double amount) {
    }

    default void renderOverlay(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {

    }

    Rectangle getBounds();

    default boolean enableClosingOnEsc(){
        return true;
    }
}
