package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonPrimitive;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptNumberArgument;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class ScriptFloatOption implements ScriptOption {

    double value = 0;

    public ScriptFloatOption(double value) {
        this.value = value;
    }

    public ScriptFloatOption() {}

    @Override
    public ScriptArgument getValue() {
        return new ScriptNumberArgument(value);
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
    public String getType() {
        return "FLOAT";
    }

    @Override
    public JsonPrimitive getJsonPrimitive() {
        return new JsonPrimitive(value);
    }
}
