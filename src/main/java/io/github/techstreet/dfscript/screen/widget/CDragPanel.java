package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.util.chat.ChatUtil;

public class CDragPanel extends CPanel {
    private double offsetX = 0;
    private double offsetY = 0;

    boolean moveOffset = false;
    double lastMouseX = 0;
    double lastMouseY = 0;

    public CDragPanel(int x, int y, int w, int h) {
        super(x,y,w,h);
    }

    @Override
    public double getOffsetCenterX() {
        return offsetX;
    }

    @Override
    public double getOffsetCenterY() {
        return offsetY;
    }

    public void setOffset(double x, double y) {
        offsetX = x;
        offsetY = y;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if(getBounds().contains(x, y)) {
            if(super.mouseClicked(x, y, button)) {
                return true;
            }

            moveOffset = true;
            lastMouseX = x;
            lastMouseY = y;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if(getBounds().contains(x, y)) {
            if(super.mouseReleased(x, y, button)) {
                return true;
            }
        }

        if(moveOffset) {
            moveOffset = false;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if(getBounds().contains(x, y)) {
            if(super.mouseDragged(x, y, button, dx, dy)) {
                return true;
            }
        }

        if(moveOffset) {
            offsetX += x - lastMouseX;
            offsetY += y - lastMouseY;
            lastMouseX = x;
            lastMouseY = y;
            return true;
        }

        return false;
    }
}
