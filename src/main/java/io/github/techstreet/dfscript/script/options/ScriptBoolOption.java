package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CTexturedButton;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.argument.ScriptTextArgument;
import io.github.techstreet.dfscript.script.values.ScriptTextValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class ScriptBoolOption implements ScriptOption {

    boolean value = false;

    public ScriptBoolOption(JsonElement value) {
        this.value = value.getAsBoolean();
    }

    public ScriptBoolOption() {}

    @Override
    public ScriptValue getValue() {
        return new ScriptTextValue(value ? "true" : "false");
    }

    @Override
    public boolean convertableTo(ScriptActionArgument.ScriptActionArgumentType arg) {
        return ScriptActionArgument.ScriptActionArgumentType.TEXT.convertableTo(arg);
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
    public JsonElement getJsonElement() {
        return new JsonPrimitive(value);
    }
}
