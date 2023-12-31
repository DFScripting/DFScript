package io.github.techstreet.dfscript.script.values;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ScriptTextValue extends ScriptValue {

    private final String value;

    public ScriptTextValue(String value) {
        this.value = value;
    }

    @Override
    String typeName() {
        return "Text";
    }

    @Override
    public String asText() {
        return value;
    }

    @Override
    public boolean asBoolean() {
        return !value.equals("");
    }

    @Override
    public boolean valueEquals(ScriptValue other) {
        return other.asText().equals(value);
    }

    @Override
    public String formatAsText() {
        return '"'+asText()+'"';
    }

    public static class Serializer implements JsonSerializer<ScriptTextValue>, JsonDeserializer<ScriptTextValue> {
        @Override
        public ScriptTextValue deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            if(jsonElement.isJsonPrimitive()) {
                JsonPrimitive prim = jsonElement.getAsJsonPrimitive();
                if(prim.isString()) {
                    return new ScriptTextValue(prim.getAsString());
                }
            }

            throw new JsonParseException("Unable to convert the json into a script text value!");
        }

        @Override
        public JsonElement serialize(ScriptTextValue scriptValue, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(scriptValue.asText());
        }
    }
}
