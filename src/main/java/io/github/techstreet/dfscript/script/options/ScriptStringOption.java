package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.values.ScriptStringValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;

public class ScriptStringOption implements ScriptOption {

    String value = "";

    public ScriptStringOption(JsonElement value) {
        this.value = value.getAsString();
    }

    public ScriptStringOption() {
    }

    @Override
    public ScriptValue getValue() {
        return new ScriptStringValue(value);
    }

    @Override
    public boolean convertableTo(ScriptActionArgument.ScriptActionArgumentType arg) {
        return ScriptActionArgument.ScriptActionArgumentType.STRING.convertableTo(arg);
    }

    @Override
    public int create(CScrollPanel panel, int x, int y, int width) {
        CTextField field = new CTextField(value, x, y, width, 10, true);
        field.setChangedListener(() -> value = field.getText());
        panel.add(field);

        return y + 12;
    }

    @Override
    public JsonElement getJsonElement() {
        return new JsonPrimitive(value);
    }

}
