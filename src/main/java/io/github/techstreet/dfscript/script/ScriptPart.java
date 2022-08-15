package io.github.techstreet.dfscript.script;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.techstreet.dfscript.script.action.ScriptAction;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.event.ScriptEvent;
import io.github.techstreet.dfscript.script.event.ScriptEventType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public interface ScriptPart {

    ScriptGroup getGroup();

    class Serializer implements JsonDeserializer<ScriptPart> {

        @Override
        public ScriptPart deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            String type = obj.get("type").getAsString();
            switch (type) {
                case "action" -> {
                    String action = obj.get("action").getAsString();
                    List<ScriptArgument> args = new ArrayList<>();
                    for (JsonElement arg : obj.get("arguments").getAsJsonArray()) {
                        args.add(context.deserialize(arg, ScriptArgument.class));
                    }
                    return new ScriptAction(ScriptActionType.valueOf(action), args);
                }
                case "event" -> {
                    String event = obj.get("event").getAsString();
                    return new ScriptEvent(ScriptEventType.valueOf(event));
                }
                default -> throw new JsonParseException("Unknown script part type: " + type);
            }
        }
    }
}
