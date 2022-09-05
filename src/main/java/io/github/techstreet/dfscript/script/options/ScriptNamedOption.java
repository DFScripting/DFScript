package io.github.techstreet.dfscript.script.options;

import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.values.ScriptValue;

public class ScriptNamedOption {
    String name;
    ScriptOption option;

    public ScriptNamedOption(ScriptOption option, String name) {
        this.name = name;
        this.option = option;
    }

    public String getFullName() {
        return getName() + " (" + option.getName() + "):";
    }

    public String getName() {
        return name;
    }

    public ScriptValue getValue() {
        return option.getValue();
    }

    public int create(CScrollPanel panel, int x, int y) {
        return option.create(panel, x, y, 105);
    }

    public void setName(String text) {
        name = text;
    }
}
