package io.github.techstreet.dfscript.script.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.render.ScriptPartRenderIconElement;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.lang.reflect.Type;
import java.util.List;

public class ScriptBuiltinCondition extends ScriptCondition {
    ScriptConditionType type;

    public ScriptBuiltinCondition(ScriptConditionType type) {
        this.type = type;
    }

    @Override
    public void create(ScriptPartRender render, Script script, String prefix, String invertedPrefix) {
        render.addElement(new ScriptPartRenderIconElement(getName(prefix, invertedPrefix), getIcon(prefix, invertedPrefix)));

        super.create(render, script, prefix, invertedPrefix);
    }

    @Override
    public boolean run(ScriptActionContext ctx) {
        return type.run(ctx) != isInverted();
    }

    @Override
    public ItemStack getIcon(String prefix, String invertedPrefix) {
        return type.getIcon((isInverted() ? invertedPrefix : prefix));
    }

    @Override
    public String getName(String prefix, String invertedPrefix) {
        return (isInverted() ? invertedPrefix : prefix) + " " + getType().getName();
    }

    @Override
    public List<Text> getLore() {
        return type.getLore();
    }

    public ScriptBuiltinCondition setType(ScriptConditionType newType) {
        type = newType;

        return this;
    }

    public ScriptConditionType getType() {
        return type;
    }

    public static class Serializer implements JsonSerializer<ScriptBuiltinCondition> {

        @Override
        public JsonElement serialize(ScriptBuiltinCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "condition");
            obj.addProperty("inverted", src.isInverted());
            obj.addProperty("condition", src.getType().name());
            return obj;
        }
    }
}
