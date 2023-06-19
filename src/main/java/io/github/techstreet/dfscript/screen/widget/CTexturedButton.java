package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.util.RenderUtil;
import java.awt.Rectangle;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class CTexturedButton extends CButton {

    private String texture;
    private final float ux;
    private final float uy;
    private final float uw;
    private final float uh;
    private final float hoverUx;
    private final float hoverUy;

    public CTexturedButton(int x, int y, int width, int height, String texture, Runnable onClick, float ux, float uy, float uw, float uh, float hoverUx, float hoverUy) {
        super(x, y, width, height, 0, "", onClick);
        this.texture = texture;
        this.ux = ux;
        this.uy = uy;
        this.uw = uw;
        this.uh = uh;
        this.hoverUx = hoverUx;
        this.hoverUy = hoverUy;
    }

    public void setTexture(String newTexture) {
        texture = newTexture;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x, y, 0);
        stack.scale(0.5f, 0.5f, 0.5f);

        Rectangle rect = new Rectangle(x, y, width, height);

        float ux = this.ux;
        float uy = this.uy;
        if (rect.contains(mouseX, mouseY)) {
            ux = hoverUx;
            uy = hoverUy;
        }

        RenderUtil.renderImage(context, 0, 0, width * 2, height * 2, ux, uy, uw, uh, texture);
        stack.pop();
    }
}
