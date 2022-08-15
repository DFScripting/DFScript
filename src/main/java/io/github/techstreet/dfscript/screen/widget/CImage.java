package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.util.RenderUtil;
import java.awt.Rectangle;
import net.minecraft.client.util.math.MatrixStack;

public class CImage implements CWidget {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final String img;

    public CImage(int x, int y, int width, int height, String img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = img;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
        RenderUtil.renderImage(stack, x, y, width, height, 0, 0, 1, 1, img);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
