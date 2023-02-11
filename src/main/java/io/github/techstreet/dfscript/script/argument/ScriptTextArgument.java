package io.github.techstreet.dfscript.script.argument;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.execution.ScriptContext;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.values.ScriptTextValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;

public record ScriptTextArgument(String value) implements ScriptArgument {

    @Override
    public ScriptValue getValue(ScriptTask task) {
        return new ScriptTextValue(value);
    }

    @Override
    public boolean convertableTo(ScriptActionArgumentType type) {
        return ScriptActionArgumentType.TEXT.convertableTo(type);
    }

    @Override
    public ItemStack getArgIcon() {
        return new ItemStack(Items.BOOK).setCustomName(Text.literal("Text").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));
    }

    @Override
    public String getArgText() {
        return value;
    }

    public static class Serializer implements JsonSerializer<ScriptTextArgument> {

        @Override
        public JsonElement serialize(ScriptTextArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("type", "TEXT");
            object.addProperty("value", src.value());
            return object;
        }
    }
}
