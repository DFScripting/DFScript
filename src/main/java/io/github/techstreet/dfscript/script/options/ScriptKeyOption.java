package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import io.github.techstreet.dfscript.screen.widget.CKeyField;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.argument.ScriptNumberArgument;
import io.github.techstreet.dfscript.script.values.ScriptNumberValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class ScriptKeyOption implements ScriptOption {

    InputUtil.Key value = null;

    public ScriptKeyOption(JsonElement value) {
        if(value.getAsString().equals("None")) {
            return;
        }

        this.value = InputUtil.fromTranslationKey(value.getAsString());
    }

    public ScriptKeyOption() {}

    @Override
    public ScriptValue getValue() {
        return new ScriptNumberValue(value.getCode());
    }

    @Override
    public boolean convertableTo(ScriptActionArgument.ScriptActionArgumentType arg) {
        return ScriptActionArgument.ScriptActionArgumentType.NUMBER.convertableTo(arg);
    }

    @Override
    public String getName() { return "Key"; }

    @Override
    public int create(CScrollPanel panel, int x, int y, int width) {
        CKeyField field = new CKeyField(x, y, width, 10, true, value);
        field.setChangedListener(() -> {
            value = field.getKey();
        });
        panel.add(field);

        return y + 12;
    }

    @Override
    public Item getIcon() {
        return Items.STONE_BUTTON;
    }

    @Override
    public JsonElement getJsonElement() {
        if(value == null) {
            return new JsonPrimitive("None");
        }

        return new JsonPrimitive(value.getTranslationKey());
    }
}
