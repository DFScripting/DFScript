package io.github.techstreet.dfscript.script.conditions;

import com.google.gson.*;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.script.*;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.action.ScriptBuiltinAction;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.repetitions.ScriptBuiltinRepetition;
import io.github.techstreet.dfscript.script.repetitions.ScriptRepetitionType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class ScriptCondition {
    boolean inverted = false;

    public void create(ScriptPartRender render, Script script) {

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

    public static class Serializer implements JsonDeserializer<ScriptCondition> {

        @Override
        public ScriptCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            boolean inverted = obj.get("inverted").getAsBoolean();
            String type = obj.get("type").getAsString();
            ScriptCondition condition;
            switch (type) {
                case "condition" -> condition = new ScriptBuiltinCondition(ScriptConditionType.valueOf(obj.get("condition").getAsString()));
                default -> throw new JsonParseException("Unknown script condition type: " + type);
            }
            if(inverted) condition.invert();
            return condition;
        }
    }
}
