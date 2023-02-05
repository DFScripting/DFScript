package io.github.techstreet.dfscript.script.render;

import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptSnippet;

public class ScriptPartRenderSnippetElement implements ScriptPartRenderElement {
    ScriptSnippet snippet;

    public ScriptPartRenderSnippetElement(ScriptSnippet snippet) {
        this.snippet = snippet;
    }

    @Override
    public int render(CScrollPanel panel, int y, int indent, Script script) {
        return snippet.create(panel, y, indent + 1, script);
    }

    @Override
    public boolean canGenerateButton() {
        return false;
    }
}
