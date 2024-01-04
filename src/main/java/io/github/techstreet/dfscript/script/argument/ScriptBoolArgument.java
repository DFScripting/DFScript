package io.github.techstreet.dfscript.script.argument;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.values.ScriptBoolValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ScriptBoolArgument implements ScriptArgument {
    private boolean value;

    public ScriptBoolArgument(boolean value) {
        this.value = value;
    }

    @Override
    public ScriptValue getValue(ScriptTask task) {
        return new ScriptBoolValue(value);
    }

    @Override
    public boolean convertableTo(ScriptActionArgumentType type) {
        return ScriptActionArgumentType.BOOL.convertableTo(type);
    }

    public static class Serializer implements JsonSerializer<ScriptBoolArgument> {

        @Override
        public JsonElement serialize(ScriptBoolArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("type", "BOOL");
            object.addProperty("value", src.value());
            return object;
        }
    }

    @Override
    public ItemStack getArgIcon() {
        return new ItemStack(value ? Items.LIME_DYE : Items.RED_DYE).setCustomName(Text.literal(value ? "True" : "False").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));
    }

    @Override
    public String getArgText() {
        return value ? "True" : "False";
    }

    @Override
    public List<ContextMenuButton> getContextMenu() {
        List<ContextMenuButton> contextMenuButtons = new ArrayList<>();

        contextMenuButtons.add(new ContextMenuButton(
                "Invert",
                () -> this.value = !this.value
        ));

        return contextMenuButtons;
    }

    public boolean value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ScriptBoolArgument) obj;
        return this.value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ScriptBoolArgument[" +
                "value=" + value + ']';
    }

}
