package io.github.techstreet.dfscript.script.argument;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.values.ScriptNumberValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public record ScriptNumberArgument(double value) implements ScriptArgument {

    @Override
    public ScriptValue getValue(ScriptTask task) {
        return new ScriptNumberValue(value);
    }

    @Override
    public boolean convertableTo(ScriptActionArgumentType type) {
        return ScriptActionArgumentType.NUMBER.convertableTo(type);
    }

    public static class Serializer implements JsonSerializer<ScriptNumberArgument> {

        @Override
        public JsonElement serialize(ScriptNumberArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("type", "NUMBER");
            object.addProperty("value", src.value());
            return object;
        }
    }

    @Override
    public ItemStack getArgIcon() {
        return new ItemStack(Items.SLIME_BALL).setCustomName(Text.literal("Number").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));
    }

    @Override
    public String getArgText() {
        String text;
        if (value % 1 == 0) {
            DecimalFormat df = new DecimalFormat("#");
            df.setRoundingMode(RoundingMode.UNNECESSARY);
            text = df.format(value);
        } else {
            text = String.valueOf(value);
        }
        return text;
    }
}
