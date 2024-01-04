package io.github.techstreet.dfscript.script.options;

import com.google.gson.*;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.util.ScriptOptionSubtypeMismatchException;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import net.minecraft.item.ItemStack;
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

    public ScriptValue getValue() {
        return option.getValue();
    }

    public ScriptOption getOption() {
        return option;
    }

    public int create(CScrollPanel panel, int x, int y) {
        return option.create(panel, x, y, 105);
    }

    public void setName(String text) {
        name = text;
    }

    public ItemStack getIcon() {
        return option.getType().getIcon().setCustomName(Text.literal(getFullName()).fillStyle(Style.EMPTY.withItalic(false)));
    }

    public static class Serializer implements JsonSerializer<ScriptNamedOption>, JsonDeserializer<ScriptNamedOption> {
        @Override
        public ScriptNamedOption deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            String name = object.get("name").getAsString();
            ScriptOptionEnum type = ScriptOptionEnum.fromName(object.get("type").getAsString());

            JsonElement value = object.get("value");

            ScriptOption option = null;

            List<ScriptOptionEnum> subtypes = new ArrayList<>();

            if (object.has("subtypes")) {
                JsonArray jsonSubtypes = object.get("subtypes").getAsJsonArray();

                for (JsonElement subtype : jsonSubtypes) {
                    subtypes.add(ScriptOptionEnum.fromName(subtype.getAsString()));
                }
            }

            try {
                option = ScriptOption.fromJson(value, type, subtypes);
            } catch (ScriptOptionSubtypeMismatchException e) {
                throw new JsonParseException("Option types don't match: " + e.getMessage());
            }

            ScriptNamedOption namedOption = new ScriptNamedOption(option, name);

            return namedOption;
        }

        @Override
        public JsonElement serialize(ScriptNamedOption src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();

            object.addProperty("name", src.name);

            object.addProperty("type", src.getOption().getType().name());

            JsonElement primitive = src.getOption().getJsonElement();

            object.add("value", primitive);

            List<ScriptOptionEnum> subtypes = src.getOption().getSubtypes();

            if (subtypes.size() != src.getOption().getType().getExtraTypes()) {
                throw new JsonParseException("Incorrect amount of extra types");
            }

            if (subtypes.size() > 0) {
                JsonArray jsonSubtypes = new JsonArray();

                subtypes.stream().forEachOrdered((a) -> jsonSubtypes.add(a.name()));

                object.add("subtypes", jsonSubtypes);
            }

            return object;
        }
    }
}
