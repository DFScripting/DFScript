package io.github.techstreet.dfscript.script.options;

import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import net.minecraft.text.Text;

public interface ScriptOption {
    ScriptValue getValue();

    String getName();

    int create(CScrollPanel panel, int x, int y, int width); // the return value = new y
}
