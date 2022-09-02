package io.github.techstreet.dfscript.script.options;

import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.values.ScriptValue;

public interface ScriptOption {
    String name = "Default Config Option Name";

    ScriptValue getValue();

    int create(CScrollPanel panel, int x, int y, int width); // the return value = new y
}
