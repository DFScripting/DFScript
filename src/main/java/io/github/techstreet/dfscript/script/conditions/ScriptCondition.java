package io.github.techstreet.dfscript.script.conditions;

import com.google.gson.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.lang.reflect.Type;
import java.util.List;

public abstract class ScriptCondition {
    boolean inverted = false;

    public void create(ScriptPartRender render, Script script, String prefix, String invertedPrefix) {

    }

    public ScriptCondition invert() {
        inverted = !inverted;
        return this;
    }

    public boolean isInverted() {
        return inverted;
    }

    public boolean run(ScriptActionContext ctx) {
        return false;
    }

    public abstract ItemStack getIcon(String prefix, String invertedPrefix);

    public abstract String getName(String prefix, String invertedPrefix);

    public abstract List<Text> getLore();

    public static class Serializer implements JsonDeserializer<ScriptCondition> {

        @Override
        public ScriptCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            boolean inverted = obj.get("inverted").getAsBoolean();
            String type = obj.get("type").getAsString();
            ScriptCondition condition;
            switch (type) {
                case "condition" ->
                        condition = new ScriptBuiltinCondition(ScriptConditionType.valueOf(obj.get("condition").getAsString()));
                default -> throw new JsonParseException("Unknown script condition type: " + type);
            }
            if (inverted) condition.invert();
            return condition;
        }
    }
}
