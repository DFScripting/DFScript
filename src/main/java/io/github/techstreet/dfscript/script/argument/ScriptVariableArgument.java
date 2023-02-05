package io.github.techstreet.dfscript.script.argument;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.execution.ScriptContext;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import java.lang.reflect.Type;

public record ScriptVariableArgument(String name) implements ScriptArgument {

    @Override
    public ScriptValue getValue(ScriptTask task) {
        return task.context().getVariable(name);
    }


    @Override
    public boolean convertableTo(ScriptActionArgumentType type) {
        return ScriptActionArgumentType.VARIABLE.convertableTo(type);
    }

    public static class Serializer implements JsonSerializer<ScriptVariableArgument> {

        @Override
        public JsonElement serialize(ScriptVariableArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("type", "VARIABLE");
            object.addProperty("value", src.name());
            return object;
        }
    }
}
