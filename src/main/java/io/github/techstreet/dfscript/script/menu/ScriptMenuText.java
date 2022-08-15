package io.github.techstreet.dfscript.script.menu;

import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.screen.widget.CWidget;
import net.minecraft.text.Text;

public class ScriptMenuText extends CText implements CWidget {

    private final String identifier;
    public ScriptMenuText(int x, int y, Text text, int color, float scale, boolean centered, boolean shadow, String identifier) {
        super(x, y, text, color, scale, centered, shadow);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
