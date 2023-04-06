package io.github.techstreet.dfscript.script.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptConfigArgument;
import io.github.techstreet.dfscript.script.event.ScriptFunction;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.render.ScriptPartRenderIconElement;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

public class ScriptFunctionCall extends ScriptAction {
    transient Script script;
    private String function;

    public ScriptFunctionCall(Script script, String function, List<ScriptArgument> arguments) {
        super(arguments);
        this.function = function;
        this.script = script;
    }

    public ScriptFunctionCall setFunction(String newFunction) {
        function = newFunction;

        return this;
    }

    @Override
    public void run(ScriptTask task) {
        getFunction().container().runSnippet(task, 0, getFunction());
    }

    public ScriptFunction getFunction() {
        return script.getNamedFunction(function);
    }

    public String getFunctionName() {
        return function;
    }

    @Override
    public boolean isDeprecated() {
        return false;
    }

    @Override
    public ItemStack getIcon() {
        return getFunction().getIcon();
    }

    @Override
    public String getName() {
        return function;
    }

    @Override
    public void create(ScriptPartRender render, Script script) {
        render.addElement(new ScriptPartRenderIconElement(getFunctionName(), getIcon()));

        super.create(render, script);
    }

    @Override
    public void updateScriptReferences(Script script) {
        super.updateScriptReferences(script);
        this.script = script;
    }

    public static class Serializer implements JsonSerializer<ScriptFunctionCall> {

        @Override
        public JsonElement serialize(ScriptFunctionCall src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "functionCall");
            obj.addProperty("functionCall", src.getFunctionName());
            obj.add("arguments", context.serialize(src.getArguments()));
            return obj;
        }
    }
}