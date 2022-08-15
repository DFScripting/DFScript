package io.github.techstreet.dfscript.script.argument;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.execution.ScriptContext;
import io.github.techstreet.dfscript.script.values.ScriptTextValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import java.lang.reflect.Type;

public record ScriptTextArgument(String value) implements ScriptArgument {

    @Override
    public ScriptValue getValue(Event event, ScriptContext context) {
        return new ScriptTextValue(value);
    }

    @Override
    public boolean convertableTo(ScriptActionArgumentType type) {
        return ScriptActionArgumentType.TEXT.convertableTo(type);
    }

    public static class Serializer implements JsonSerializer<ScriptTextArgument> {

        @Override
        public JsonElement serialize(ScriptTextArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("type", "TEXT");
            object.addProperty("value", src.value());
            return object;
        }
    }
}
