package io.github.techstreet.dfscript.script.values;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ScriptBoolValue extends ScriptValue {

    private final boolean value;

    public ScriptBoolValue(boolean value) {
        this.value = value;
    }

    @Override
    String typeName() {
        return "Boolean";
    }

    @Override
    public String asString() {
        return value ? "true" : "false";
    }

    @Override
    public double asNumber() {
        return value ? 1 : 0;
    }

    @Override
    public boolean asBoolean() {
        return value;
    }

    @Override
    public boolean valueEquals(ScriptValue other) {
        if (!(other.get() instanceof ScriptNumberValue)
                && !(other.get() instanceof ScriptUnknownValue)) {
            return false;
        }
        return other.asBoolean() == value;
    }

    @Override
    public String formatAsText() {
        return value ? "True" : "False";
    }

    public static class Serializer implements JsonSerializer<ScriptBoolValue>, JsonDeserializer<ScriptBoolValue> {
        @Override
        public ScriptBoolValue deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            if(jsonElement.isJsonPrimitive()) {
                JsonPrimitive prim = jsonElement.getAsJsonPrimitive();
                if(prim.isBoolean()) {
                    return new ScriptBoolValue(prim.getAsBoolean());
                }
            }

            throw new JsonParseException("Unable to convert the json into a script text value!");
        }

        @Override
        public JsonElement serialize(ScriptBoolValue scriptValue, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(scriptValue.value);
        }
    }
}
