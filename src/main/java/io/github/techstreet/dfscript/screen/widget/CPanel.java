package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class CPanel implements CWidget {
    private final List<CWidget> children = new ArrayList<>();
    private final int x, y, width, height;

    public CPanel(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public abstract double getOffsetCenterX();
    public abstract double getOffsetCenterY();

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        MatrixStack stack = context.getMatrices();
        stack.push();

        stack.translate(x, y, 0);
        mouseX -= x;
        mouseY -= y;

        float xPos = stack.peek().getPositionMatrix().m30();
        float yPos = stack.peek().getPositionMatrix().m31();

        Vector4f begin = new Vector4f(xPos, yPos, 1, 1);
        Vector4f end = new Vector4f(xPos + (width * 2), yPos + (height * 2), 1, 1);

        int guiScale = (int) DFScript.MC.getWindow().getScaleFactor();
        RenderUtil.pushScissor(
                (int) begin.x()*guiScale,
                (int) begin.y()*guiScale,
                (int) (end.x() - begin.x())*guiScale,
                (int) (end.y() - begin.y())*guiScale
        );

        double scroll = getOffsetCenterY();
        double hScroll = getOffsetCenterX();
        stack.translate(hScroll, scroll, 0);
        mouseY -= scroll;
        mouseX -= hScroll;

        for (CWidget child : children) {
            child.render(context, mouseX, mouseY, tickDelta);
        }

        RenderUtil.popScissor();
        stack.pop();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if(!getBounds().contains(x, y)) {
            return false;
        }

        x -= getOffsetCenterX();
        y -= getOffsetCenterY();
        x -= this.x;
        y -= this.y;

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseClicked(x, y, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if(!getBounds().contains(x, y)) {
            return false;
        }

        x -= getOffsetCenterX();
        y -= getOffsetCenterY();
        x -= this.x;
        y -= this.y;

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseReleased(x, y, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if(!getBounds().contains(x, y)) {
            return false;
        }

        x -= getOffsetCenterX();
        y -= getOffsetCenterY();
        x -= this.x;
        y -= this.y;

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseDragged(x, y, button, dx, dy)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void charTyped(char ch, int keyCode) {
        for (CWidget child : children) {
            child.charTyped(ch, keyCode);
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        for (CWidget child : children) {
            child.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {
        for (CWidget child : children) {
            child.keyReleased(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double vertical, double horizontal) {
        if(!getBounds().contains(mouseX, mouseY)) {
            return false;
        }

        mouseX -= getOffsetCenterX();
        mouseY -= getOffsetCenterY();
        mouseX -= this.x;
        mouseY -= this.y;

        for (CWidget child : children) {
            if(child.mouseScrolled(mouseX, mouseY, vertical, horizontal)) {
                return true;
            }
        }
        return false;
    }

    public void add(CWidget child) {
        children.add(child);
    }

    public void clear() { children.clear(); }

    @Override
    public void renderOverlay(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x, y, 0);
        mouseX -= x;
        mouseY -= y;
        double scroll = getOffsetCenterY();
        double hScroll = getOffsetCenterX();
        if(mouseX < 0 || mouseX > width) {
            mouseX = -99999;
        }
        else {
            mouseX -= hScroll;
        }
        if(mouseY < 0 || mouseY > height) {
            mouseY = -99999;
        }
        else {
            mouseY -= scroll;
        }
        stack.translate(hScroll, scroll, 0);
        for (CWidget child : children) {
            child.renderOverlay(context, mouseX, mouseY, tickDelta);
        }
        stack.pop();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public CWidget[] getChildren() {
        return children.toArray(new CWidget[0]);
    }

    @Override
    public boolean enableClosingOnEsc() {
        for(CWidget widget : children) {
            if(!widget.enableClosingOnEsc())
            {
                return false;
            }
        }

        return CWidget.super.enableClosingOnEsc();
    }

    public void remove(CWidget w) {
        children.remove(w);
    }
}
