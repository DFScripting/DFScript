package io.github.techstreet.dfscript.script.argument;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.execution.ScriptContext;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import java.lang.reflect.Type;

public interface ScriptArgument {

    ScriptValue getValue(ScriptTask task);

    boolean convertableTo(ScriptActionArgumentType type);

    class Serializer implements JsonDeserializer<ScriptArgument> {

        @Override
        public ScriptArgument deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            String type = object.get("type").getAsString();
            return switch (type) {
                case "TEXT" -> new ScriptTextArgument(object.get("value").getAsString());
                case "NUMBER" -> new ScriptNumberArgument(object.get("value").getAsDouble());
                case "VARIABLE" -> new ScriptVariableArgument(object.get("value").getAsString());
                case "CLIENT_VALUE" -> ScriptClientValueArgument.valueOf(object.get("value").getAsString());
                case "CONFIG_VALUE" -> new ScriptConfigArgument(object.get("value").getAsString(), null);
                default -> throw new JsonParseException("Unknown argument type: " + type);
            };
        }
    }
}
