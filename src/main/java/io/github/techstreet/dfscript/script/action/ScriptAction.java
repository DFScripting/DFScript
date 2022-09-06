package io.github.techstreet.dfscript.script.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptGroup;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptConfigArgument;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.execution.ScriptContext;
import io.github.techstreet.dfscript.script.execution.ScriptScopeVariables;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ScriptAction implements ScriptPart {

    private ScriptActionType type;
    private final List<ScriptArgument> arguments;

    public ScriptAction(ScriptActionType type, List<ScriptArgument> arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    public ScriptAction setType(ScriptActionType newType) {
        type = newType;

        return this;
    }

    public void invoke(Event event, ScriptContext context, Consumer<ScriptScopeVariables> inner, ScriptTask task, Script script) {
        type.run(new ScriptActionContext(
            context, arguments, event, inner, task, new HashMap<>(), script
        ));
    }

    public ScriptActionType getType() {
        return type;
    }

    public List<ScriptArgument> getArguments() {
        return arguments;
    }

    @Override
    public ScriptGroup getGroup() {
        return getType().getGroup();
    }

    public void updateScriptReferences(Script script) {
        for(ScriptArgument arg : getArguments()) {
            if (arg instanceof ScriptConfigArgument carg) {
                carg.setScript(script);
            }
        }
    }

    public static class Serializer implements JsonSerializer<ScriptAction> {

        @Override
        public JsonElement serialize(ScriptAction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "action");
            obj.addProperty("action", src.getType().name());
            obj.add("arguments", context.serialize(src.getArguments()));
            return obj;
        }
    }
}