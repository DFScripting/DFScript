package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonPrimitive;
import io.github.techstreet.dfscript.screen.widget.CKeyField;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptNumberArgument;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class ScriptKeyOption implements ScriptOption {

    InputUtil.Key value = null;

    public ScriptKeyOption(InputUtil.Key value) {
        this.value = value;
    }

    public ScriptKeyOption() {}

    @Override
    public ScriptArgument getValue() {
        return new ScriptNumberArgument(value.getCode());
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
    public String getType() {
        return "KEY";
    }

    @Override
    public JsonPrimitive getJsonPrimitive() {
        return new JsonPrimitive(value.getTranslationKey());
    }
}
