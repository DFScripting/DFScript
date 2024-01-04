package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CTexturedButton;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.values.ScriptTextValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;

public class ScriptLegacyBoolOption implements ScriptOption {

    boolean value = false;

    public ScriptLegacyBoolOption(JsonElement value) {
        this.value = value.getAsBoolean();
    }

    public ScriptLegacyBoolOption() {
    }

    @Override
    public ScriptValue getValue() {
        return new ScriptTextValue(value ? "true" : "false");
    }

    @Override
    public boolean convertableTo(ScriptActionArgument.ScriptActionArgumentType arg) {
        return ScriptActionArgument.ScriptActionArgumentType.TEXT.convertableTo(arg);
    }

    @Override
    public int create(CScrollPanel panel, int x, int y, int width) {
        CTexturedButton button = new CTexturedButton(x, y, 8, 8, getTexture(), getHighlightedTexture(), null);
        button.setOnClick(() -> {
            value = !value;
            button.setTexture(getTexture(), getHighlightedTexture());
        });
        panel.add(button);

        return y + 10;
    }

    private String getTexture() {
        return DFScript.MOD_ID + (value ? ":on_button.png" : ":off_button.png");
    }

    private String getHighlightedTexture() {
        return DFScript.MOD_ID + (value ? ":on_button_highlight.png" : ":disable_highlight.png");
    }

    @Override
    public JsonElement getJsonElement() {
        return new JsonPrimitive(value);
    }
}
