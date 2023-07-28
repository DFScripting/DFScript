package io.github.techstreet.dfscript.script.action;

import com.google.gson.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;

public class ScriptActionArgument {

    private String name;
    private final ScriptActionArgumentType type;
    private boolean optional = false;
    private boolean plural = false;

    public ScriptActionArgument(String name, ScriptActionArgumentType type) {
        this.name = name;
        this.type = type;
    }

    public ScriptActionArgument optional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public ScriptActionArgument plural(boolean plural) {
        this.plural = plural;
        return this;
    }

    public String name() {
        return name;
    }

    public boolean optional() {
        return optional;
    }

    public boolean plural() {
        return plural;
    }

    public ScriptActionArgumentType type() {
        return type;
    }

    public Text text() {
        MutableText t = type.text();
        if (plural) {
            t.append(Text.literal("(s)").fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE)));
        }
        if (optional) {
            t.append(Text.literal("*").fillStyle(Style.EMPTY.withItalic(true)));
        }
        return t.append(Text.literal(" - ").fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GRAY)))
            .append(Text.literal(name).fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE)));
    }

    public void setName(String text) {
        name = text;
    }

    public enum ScriptActionArgumentType {
        VARIABLE("Variable", Items.MAGMA_CREAM),
        NUMBER("Number", Items.SLIME_BALL),
        TEXT("Text", Items.BOOK),
        LIST("List", Items.CHEST),
        DICTIONARY("Dictionary", Items.CHEST_MINECART),
        ANY("Any", Items.ENDER_EYE);

        private final String name;
        private final Item icon;

        ScriptActionArgumentType(String name, Item icon) {
            this.name = name;
            this.icon = icon;
        }

        public MutableText text() {
            MutableText val = Text.literal(name);
            return val.fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE));
        }
        public boolean convertableTo(ScriptActionArgumentType to) {
            return to == ANY
                || to == TEXT
                || this == VARIABLE
                || this == to;
        }

        public ItemStack icon() {
            ItemStack itemStack = new ItemStack(icon);

            itemStack.setCustomName(text());

            return itemStack;
        }
    }

    public static class Serializer implements JsonSerializer<ScriptActionArgument>, JsonDeserializer<ScriptActionArgument> {

        @Override
        public JsonElement serialize(ScriptActionArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();

            obj.addProperty("name", src.name());
            obj.addProperty("type", src.type().name());
            obj.addProperty("optional", src.optional());
            obj.addProperty("plural", src.plural());

            return obj;
        }

        @Override
        public ScriptActionArgument deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            String name = obj.get("name").getAsString();
            ScriptActionArgumentType argType = ScriptActionArgumentType.valueOf(obj.get("type").getAsString());
            boolean optional = obj.get("optional").getAsBoolean();
            boolean plural = obj.get("plural").getAsBoolean();

            return new ScriptActionArgument(name, argType).optional(optional).plural(plural);
        }
    }
}