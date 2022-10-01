package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.util.RenderUtil;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vector4f;

public class CPlainPanel implements CWidget {

    private final List<CWidget> children = new ArrayList<>();
    private final int x, y, width, height;

    public CPlainPanel(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
        stack.push();
        stack.translate(x, y, 0);

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

        for (CWidget child : children) {
            child.render(stack, mouseX, mouseY, tickDelta);
        }

        RenderUtil.popScissor();
        stack.pop();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseClicked(x, y, button)) {
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
    public void mouseScrolled(double mouseX, double mouseY, double amount) {
        for (CWidget child : children) {
            child.mouseScrolled(mouseX, mouseY, amount);
        }
    }

    private int getMaxScroll() {
        int max = 0;
        for (CWidget child : children) {
            max = Math.max(max, child.getBounds().y + child.getBounds().height);
        }
        return max - height;
    }

    public void add(CWidget child) {
        children.add(child);
    }

    public void clear() { children.clear(); }

    @Override
    public void renderOverlay(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
        stack.push();
        stack.translate(x, y, 0);
        for (CWidget child : children) {
            child.renderOverlay(stack, mouseX, mouseY, tickDelta);
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
}
