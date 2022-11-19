package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.argument.ScriptTextArgument;
import io.github.techstreet.dfscript.script.values.ScriptTextValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class ScriptTextOption implements ScriptOption {

    String value = "";

    public ScriptTextOption(JsonElement value) {
        this.value = value.getAsString();
    }

    public ScriptTextOption() {}

    @Override
    public ScriptValue getValue() {
        return new ScriptTextValue(value);
    }

    @Override
    public boolean convertableTo(ScriptActionArgument.ScriptActionArgumentType arg) {
        return ScriptActionArgument.ScriptActionArgumentType.TEXT.convertableTo(arg);
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
