package io.github.techstreet.dfscript.script.render;

import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.event.ScriptHeader;

import java.util.function.Function;

public class ScriptPartRenderDynamicElement implements ScriptPartRenderElement {
    private Function<ScriptPartRenderArgs, Integer> onRender;

    public ScriptPartRenderDynamicElement(Function<ScriptPartRenderArgs, Integer> onRender) {
        this.onRender = onRender;
    }

    @Override
    public int render(CScrollPanel panel, int y, int indent, Script script, ScriptHeader header) {
        return onRender.apply(new ScriptPartRenderArgs(panel, y, indent, script));
    }

    public record ScriptPartRenderArgs(CScrollPanel panel, int y, int indent, Script script) {
        public int y() {
            return y;
        }

        public int indent() {
            return indent;
        }

        public CScrollPanel panel() {
            return panel;
        }

        public Script script() {
            return script;
        }
    }
}