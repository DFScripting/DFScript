package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonPrimitive;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.screen.widget.CTexturedButton;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptTextArgument;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class ScriptBoolOption implements ScriptOption {

    boolean value = false;

    public ScriptBoolOption(boolean value) {
        this.value = value;
    }

    public ScriptBoolOption() {}

    @Override
    public ScriptArgument getValue() {
        return new ScriptTextArgument(value ? "true" : "false");
    }

    @Override
    public String getName() { return "Boolean"; }

    @Override
    public int create(CScrollPanel panel, int x, int y, int width) {
        CTexturedButton button = new CTexturedButton(x, y, 8, 8, getTexture(), null, 0, 0, 1, 0.5f, 0, 0.5f);
        button.setOnClick(() -> {
            value = !value;
            button.setTexture(getTexture());
        });
        panel.add(button);

        return y + 10;
    }

    private String getTexture() {
        return DFScript.MOD_ID + (value ? ":on_button.png" : ":off_button.png");
    }

    @Override
    public Item getIcon() {
        return Items.LEVER;
    }

    @Override
    public String getType() {
        return "BOOL";
    }

    @Override
    public JsonPrimitive getJsonPrimitive() {
        return new JsonPrimitive(value);
    }
}
