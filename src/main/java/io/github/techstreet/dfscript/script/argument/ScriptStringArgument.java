package io.github.techstreet.dfscript.script.argument;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.values.ScriptStringValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;

public record ScriptStringArgument(String value) implements ScriptArgument {

    @Override
    public ScriptValue getValue(ScriptTask task) {
        return new ScriptStringValue(value);
    }

    @Override
    public boolean convertableTo(ScriptActionArgumentType type) {
        return ScriptActionArgumentType.STRING.convertableTo(type);
    }

    @Override
    public ItemStack getArgIcon() {
        return new ItemStack(Items.STRING).setCustomName(Text.literal("String").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));
    }

    @Override
    public String getArgText() {
        return value;
    }

    public static class Serializer implements JsonSerializer<ScriptStringArgument> {

        @Override
        public JsonElement serialize(ScriptStringArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("type", "STRING");
            object.addProperty("value", src.value());
            return object;
        }
    }

}
