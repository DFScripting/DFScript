package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.argument.ScriptNumberArgument;
import io.github.techstreet.dfscript.script.values.ScriptNumberValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class ScriptFloatOption implements ScriptOption {

    double value = 0;

    public ScriptFloatOption(JsonElement value) {
        this.value = value.getAsDouble();
    }

    public ScriptFloatOption() {}

    @Override
    public ScriptValue getValue() {
        return new ScriptNumberValue(value);
    }

    @Override
    public boolean convertableTo(ScriptActionArgument.ScriptActionArgumentType arg) {
        return ScriptActionArgument.ScriptActionArgumentType.NUMBER.convertableTo(arg);
    }
    @Override
    public String getName() { return "Floating-Point Value"; }

    @Override
    public int create(CScrollPanel panel, int x, int y, int width) {
        CTextField field = new CTextField(String.valueOf(value), x, y, width, 10, true);
        field.setChangedListener(() -> {
            try {
                value = Double.parseDouble(field.getText());
                field.textColor = 0xFFFFFF;
            }
            catch(Exception e) {
                field.textColor = 0xFF3333;
            }
        });
        panel.add(field);

        return y + 12;
    }

    @Override
    public Item getIcon() {
        return Items.SLIME_BLOCK;
    }

    @Override
    public JsonElement getJsonElement() {
        return new JsonPrimitive(value);
    }
}
