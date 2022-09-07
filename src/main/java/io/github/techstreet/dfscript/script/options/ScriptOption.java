package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import net.minecraft.item.Item;

public interface ScriptOption {
    ScriptArgument getValue();

    String getName();

    int create(CScrollPanel panel, int x, int y, int width); // the return value = new y

    Item getIcon();

    String getType();

    JsonPrimitive getJsonPrimitive();
}
