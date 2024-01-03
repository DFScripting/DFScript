package io.github.techstreet.dfscript.script.values;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class ScriptStringValue extends ScriptValue {

    private final String value;

    public ScriptStringValue(String value) {
        this.value = value;
    }

    @Override
    String typeName() {
        return "String";
    }

    @Override
    public ScriptValue convertTo(ScriptValue type) {
        if (type instanceof ScriptDictionaryValue) {
            HashMap<String, ScriptValue> map = new HashMap<>();
            map.put("String", this);
            return new ScriptDictionaryValue(map);
        } else if (type instanceof ScriptListValue) {
            List<ScriptValue> list = new java.util.ArrayList<>(List.of());
            for (char c : value.toCharArray()) {
                list.add(new ScriptStringValue(String.valueOf(c)));
            }
            return new ScriptListValue(list);
        } else if (type instanceof ScriptBoolValue) {
            return new ScriptBoolValue(asBoolean());
        } else {
            return super.convertTo(type);
        }
    }


    @Override
    public String asString() {
        return value;
    }

    @Override
    public boolean asBoolean() {
        return !value.equals("");
    }

    @Override
    public boolean valueEquals(ScriptValue other) {
        return other.asString().equals(value);
    }

    @Override
    public String formatAsText() {
        return "'" + asString() + "'";
    }

    public static class Serializer implements JsonSerializer<ScriptStringValue>, JsonDeserializer<ScriptStringValue> {
        @Override
        public ScriptStringValue deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            return new ScriptStringValue(jsonElement.getAsString());
        }

        @Override
        public JsonElement serialize(ScriptStringValue scriptValue, Type type, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.add("string", new JsonPrimitive(scriptValue.value));
            obj.addProperty("___objectType", "string");
            return obj;
        }
    }
}
