package io.github.techstreet.dfscript.script.render;

import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.event.ScriptHeader;

public interface ScriptPartRenderElement {
    int render(CScrollPanel panel, int y, int indent, Script script, ScriptHeader header);

    default boolean canGenerateButton() {
        return true;
    }
}
