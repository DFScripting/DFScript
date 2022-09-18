package io.github.techstreet.dfscript.script.options;

import com.google.gson.*;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScriptNamedOption {
    String name;
    ScriptOption option;

    public ScriptNamedOption(ScriptOption option, String name) {
        this.name = name;
        this.option = option;
    }

    public String getFullName() {
        return getName() + " (" + option.getName() + ")";
    }

    public String getName() {
        return name;
    }

    public ScriptArgument getValue() {
        return option.getValue();
    }

    public ScriptOption getOption() { return option; }

    public int create(CScrollPanel panel, int x, int y) {
        return option.create(panel, x, y, 105);
    }

    public void setName(String text) {
        name = text;
    }

    public ItemStack getIcon() {
        return new ItemStack(option.getIcon()).setCustomName(new LiteralText(getName()).fillStyle(Style.EMPTY.withItalic(false)));
    }

    public static class Serializer implements JsonSerializer<ScriptNamedOption>, JsonDeserializer<ScriptNamedOption> {
        @Override
        public ScriptNamedOption deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            String name = object.get("name").getAsString();
            String type = object.get("type").getAsString();

            ScriptOption option;

            switch(type)
            {
                case "TEXT" -> option = new ScriptTextOption(object.get("value").getAsString());
                case "INT" -> option = new ScriptIntOption(object.get("value").getAsInt());
                case "FLOAT" -> option = new ScriptFloatOption(object.get("value").getAsDouble());
                case "KEY" -> option = new ScriptKeyOption(InputUtil.fromTranslationKey(object.get("value").getAsString()));
                default -> throw new JsonParseException("Unknown option type: " + type);
            }

            ScriptNamedOption namedOption = new ScriptNamedOption(option, name);

            return namedOption;
        }

        @Override
        public JsonElement serialize(ScriptNamedOption src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();

            object.addProperty("name", src.name);

            object.addProperty("type", src.getOption().getType());

            JsonPrimitive primitive = src.getOption().getJsonPrimitive();

            object.add("value", primitive);

            return object;
        }
    }
}
