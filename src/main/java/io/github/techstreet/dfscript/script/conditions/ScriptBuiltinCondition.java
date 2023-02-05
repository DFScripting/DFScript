package io.github.techstreet.dfscript.script.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptContainer;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.action.ScriptAction;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.render.ScriptPartRenderIconElement;
import net.minecraft.text.Text;

import java.lang.reflect.Type;
import java.util.List;

public class ScriptBuiltinCondition extends ScriptCondition {
    ScriptConditionType type;

    public ScriptBuiltinCondition(ScriptConditionType type) {
        this.type = type;
    }

    @Override
    public void create(ScriptPartRender render, Script script) {
        render.addElement(new ScriptPartRenderIconElement((isInverted() ? "Unless " : "If ") + getType().getName(), getType().getIcon()));

        super.create(render, script);
    }

    @Override
    public boolean run(ScriptActionContext ctx) {
        return type.run(ctx) != isInverted();
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
