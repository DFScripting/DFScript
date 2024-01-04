package io.github.techstreet.dfscript.script.values;

import com.google.gson.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

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
    public ScriptValue convertTo(ScriptValue type) {
        if (type instanceof ScriptDictionaryValue) {
            HashMap<String, ScriptValue> map = new HashMap<>();
            map.put("String", this);
            return new ScriptDictionaryValue(map);
        } else if (type instanceof ScriptListValue) {
            List<ScriptValue> list = new java.util.ArrayList<>(List.of());
            for (char c : value.toCharArray()) {
                list.add(new ScriptTextValue(String.valueOf(c)));
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
        return '"' + asString() + '"';
    }

    public Component parse() {
        return MiniMessage.miniMessage().deserialize(value);
    }

    public static class Serializer implements JsonSerializer<ScriptTextValue>, JsonDeserializer<ScriptTextValue> {
        @Override
        public ScriptTextValue deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (jsonElement.isJsonPrimitive()) {
                JsonPrimitive prim = jsonElement.getAsJsonPrimitive();
                if (prim.isString()) {
                    return new ScriptTextValue(prim.getAsString());
                }
            }

            throw new JsonParseException("Unable to convert the json into a script text value!");
        }

        @Override
        public JsonElement serialize(ScriptTextValue scriptValue, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(scriptValue.asString());
        }
    }
}
