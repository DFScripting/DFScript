package io.github.techstreet.dfscript.script.render;

import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptSnippet;
import io.github.techstreet.dfscript.script.event.ScriptHeader;

public class ScriptPartRenderSnippetElement implements ScriptPartRenderElement {
    ScriptSnippet snippet;

    public ScriptPartRenderSnippetElement(ScriptSnippet snippet) {
        this.snippet = snippet;
    }

    @Override
    public int render(CScrollPanel panel, int y, int indent, Script script, ScriptHeader header) {
        return snippet.create(panel, y, indent + 1, script, header);
    }

    @Override
    public boolean canGenerateButton() {
        return false;
    }
}
