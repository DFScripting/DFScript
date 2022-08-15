package io.github.techstreet.dfscript.script.menu;

import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CWidget;
import io.github.techstreet.dfscript.script.Script;

public class ScriptMenu extends CScreen {

    private final Script script;

    public ScriptMenu(int width, int height, Script script) {
        super(width, height);
        this.script = script;
    }

    public boolean ownedBy(Script script) {
        return this.script == script;
    }

    public void removeChild(String identifier) {
        widgets.removeIf(widget -> {
            if (widget instanceof ScriptWidget sw) {
                return sw.getIdentifier().equals(identifier);
            }
            return false;
        });
    }

    public ScriptWidget getWidget(String identifier) {
        for (CWidget widget : widgets) {
            if (widget instanceof ScriptWidget sw) {
                if (sw.getIdentifier().equals(identifier)) {
                    return sw;
                }
            }
        }
        return null;
    }
}
