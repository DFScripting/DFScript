package io.github.techstreet.dfscript.script.render;

import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.Script;

public interface ScriptPartRenderElement {
    int render(CScrollPanel panel, int y, int indent, Script script);

    default boolean canGenerateButton() {
        return true;
    }
}
