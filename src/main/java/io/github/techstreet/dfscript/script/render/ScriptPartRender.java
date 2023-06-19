package io.github.techstreet.dfscript.script.render;

import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CWidget;
import io.github.techstreet.dfscript.script.Script;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScriptPartRender {
    List<ScriptPartRenderElement> elements = new ArrayList<>();
    List<ScriptButtonPos> buttonPos = new ArrayList<>();

    public ScriptPartRender() {

    }

    public ScriptPartRender addElement(ScriptPartRenderElement element) {
        elements.add(element);
        return this;
    }

    public List<ScriptButtonPos> getButtonPositions() {
        return buttonPos;
    }

    public int create(CScrollPanel panel, int y, int indent, Script script) {
        buttonPos.clear();
        for (ScriptPartRenderElement element : elements) {
            int origY = y;
            y = element.render(panel, y, indent, script);
            if(element.canGenerateButton()) {
                createIndent(panel, indent, origY, y - origY - 2);
                buttonPos.add(new ScriptButtonPos(origY, y - origY));
            }
        }
        return y;
    }

    public static void createIndent(CScrollPanel panel, int indent, int y, int height)
    {
        for (int i = 0; i < indent; i ++) {
            int xpos = 8 + i*5;
            int ypos = y;
            panel.add(new CWidget() {
                @Override
                public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
                    context.fill(xpos, ypos, xpos + 1, ypos + height, 0xFF333333);
                }

                @Override
                public Rectangle getBounds() {
                    return new Rectangle(0, 0, 0, 0);
                }
            });
        }
    }

    public record ScriptButtonPos(int y, int height) {
        public int getY() {
            return y;
        }

        public int getHeight() {
            return height;
        }
    }
}