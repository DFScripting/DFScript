package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.util.RenderUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vector4f;

import java.awt.*;

public class CKeyField implements CWidget {

    final int x, y, width, height;

    boolean selected;
    boolean editable;
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
    public void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
        stack.push();
        stack.translate(x, y, 0);

        DrawableHelper.fill(stack, 0, 0, width, height, 0xFF888888);
        DrawableHelper.fill(stack, 1, 1, width - 1, height - 1, 0xFF000000);

        Vector4f begin = new Vector4f(0, 0, 1, 1);
        Vector4f end = new Vector4f(width, height, 1, 1);
        begin.transform(stack.peek().getPositionMatrix());
        end.transform(stack.peek().getPositionMatrix());

        int guiScale = (int) DFScript.MC.getWindow().getScaleFactor();
        RenderUtil.pushScissor(
                (int) begin.getX()*guiScale,
                (int) begin.getY()*guiScale,
                (int) (end.getX() - begin.getX())*guiScale,
                (int) (end.getY() - begin.getY())*guiScale
        );

        stack.translate(2, 2, 0);
        stack.scale(0.5f, 0.5f, 0);

        TextRenderer f = DFScript.MC.textRenderer;

        stack.push();

        String line = null;
        int color = textColor;

        if(key != null) {
            line = key.getLocalizedText().getString();
        }

        if(editable && selected) {
            color = 0xFFFF00;
        }

        if(line != null) {
            f.draw(stack, line, 0, 0, color);
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
                }
                else {
                    this.selected = false;
                }
            }
        }
        else {
            this.selected = false;
        }
        return false;
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if(editable && selected) {
            if(keyCode != -1) {
                if(keyCode == 10) {
                    key = null;
                }
                else
                {
                    key = InputUtil.fromKeyCode(keyCode, scanCode);
                }

                changedListener.run();
            }

            selected = false;
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
}

