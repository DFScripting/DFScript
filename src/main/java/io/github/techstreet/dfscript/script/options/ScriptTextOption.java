package io.github.techstreet.dfscript.script.options;

import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.values.ScriptTextValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;

public class ScriptTextOption implements ScriptOption {

    String value = "";

    @Override
    public ScriptValue getValue() {
        return new ScriptTextValue(value);
    }

    @Override
    public String getName() { return "Text"; }

    @Override
    public int create(CScrollPanel panel, int x, int y, int width) {
        CTextField field = new CTextField(value, x, y, width, 10, true);
        field.setChangedListener(() -> value = field.getText());
        panel.add(field);

        return y + 12;
    }
}
