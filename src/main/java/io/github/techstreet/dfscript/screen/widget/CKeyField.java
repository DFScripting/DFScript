package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.util.RenderUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector4f;

import java.awt.*;

public class CKeyField implements CWidget {

    final int x, y, width, height;

    boolean selected;
    boolean editable;

    boolean blockEsc = false;
    public int textColor = 0xFFFFFFFF;
    InputUtil.Key key;
    Runnable changedListener;

    public CKeyField(int x, int y, int width, int height, boolean editable) {
        this(x, y, width, height, editable, null);
    }

    public CKeyField(int x, int y, int width, int height, boolean editable, InputUtil.Key key) {
        this.key = key;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.editable = editable;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x, y, 0);

        int outlineColor = 0xFF888888;

        if (editable && selected) {
            outlineColor = 0xFFFFFF00;
        }

        context.fill(0, 0, width, height, outlineColor);
        context.fill(1, 1, width - 1, height - 1, 0xFF000000);

        float xPos = stack.peek().getPositionMatrix().m30();
        float yPos = stack.peek().getPositionMatrix().m31();

        Vector4f begin = new Vector4f(xPos - 2, yPos + 2, 1, 1);
        Vector4f end = new Vector4f((xPos + (width * 2)) - 7, (yPos + (height * 2)), 1, 1);

        int guiScale = (int) DFScript.MC.getWindow().getScaleFactor();
        RenderUtil.pushScissor(
                (int) begin.x() * guiScale,
                (int) begin.y() * guiScale,
                (int) (end.x() - begin.x()) * guiScale,
                (int) (end.y() - begin.y()) * guiScale
        );

        stack.translate(2, 2, 0);
        stack.scale(0.5f, 0.5f, 0);

        TextRenderer f = DFScript.MC.textRenderer;

        stack.push();

        String line;
        int color = textColor;

        if (key != null) {
            line = key.getLocalizedText().getString();
        } else {
            line = "None";
        }

        if (editable && selected) {
            color = 0xFFFF00;
        }

        if (line != null) {
            context.drawText(f, line, 0, 0, color, false);
        }

        stack.translate(0, f.fontHeight, 0);

        stack.pop();
        stack.pop();
        RenderUtil.popScissor();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (editable) {
            if (button == 0) {
                if (x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height) {
                    this.selected = true;
                    blockEsc = true;
                } else {
                    this.selected = false;
                    blockEsc = false;
                }
            }
        } else {
            this.selected = false;
            blockEsc = false;
        }
        return false;
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (editable && selected) {
            if (keyCode != -1) {
                if (keyCode == 256) {
                    key = null;
                } else {
                    key = InputUtil.fromKeyCode(keyCode, scanCode);
                }

                changedListener.run();
            }

            selected = false;
            blockEsc = true;
        } else {
            blockEsc = false;
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void setChangedListener(Runnable r) {
        changedListener = r;
    }

    public InputUtil.Key getKey() {
        return key;
    }

    public void setKey(InputUtil.Key k) {
        key = k;
    }

    @Override
    public boolean enableClosingOnEsc() {
        return !blockEsc;
    }
}