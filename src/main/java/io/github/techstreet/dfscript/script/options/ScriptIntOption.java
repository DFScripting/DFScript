package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonPrimitive;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptNumberArgument;
import io.github.techstreet.dfscript.script.argument.ScriptTextArgument;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class ScriptIntOption implements ScriptOption {

    int value = 0;

    public ScriptIntOption(int value) {
        this.value = value;
    }

    public ScriptIntOption() {}

    @Override
    public ScriptArgument getValue() {
        return new ScriptNumberArgument(value);
    }

    @Override
    public String getName() { return "Integer"; }

    @Override
    public int create(CScrollPanel panel, int x, int y, int width) {
        CTextField field = new CTextField(String.valueOf(value), x, y, width, 10, true);
        field.setChangedListener(() -> {
            try {
                value = Integer.parseInt(field.getText());
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
        return Items.SLIME_BALL;
    }

    @Override
    public String getType() {
        return "INT";
    }

    @Override
    public JsonPrimitive getJsonPrimitive() {
        return new JsonPrimitive(value);
    }
}
