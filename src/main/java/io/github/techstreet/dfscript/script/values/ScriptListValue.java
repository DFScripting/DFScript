package io.github.techstreet.dfscript.script.values;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScriptListValue extends ScriptValue {

    private final List<ScriptValue> value;

    public ScriptListValue(List<ScriptValue> value) {
        this.value = value;
    }

    @Override
    String typeName() {
        return "List";
    }

    @Override
    public List<ScriptValue> asList() {
        return new ArrayList<>(value);
    }

    @Override
    public boolean valueEquals(ScriptValue other) {
        if (!(other.get() instanceof ScriptListValue)
            && !(other.get() instanceof ScriptUnknownValue)) {
            return false;
        }
        List<ScriptValue> otherList = other.asList();
        if (otherList.size() != value.size()) {
            return false;
        }
        for (int i = 0; i < value.size(); i++) {
            if (!value.get(i).valueEquals(otherList.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String asString() {
        return value.toString();
    }

    @Override
    public boolean asBoolean() {
        return !value.isEmpty();
    }

    @Override
    public ScriptValue getCompareValue() {
        return asList().get(0).getCompareValue();
    }

    public static class Serializer implements JsonSerializer<ScriptListValue>, JsonDeserializer<ScriptListValue> {
        @Override
        public ScriptListValue deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            if(jsonElement.isJsonArray()) {
                JsonArray arr = jsonElement.getAsJsonArray();
                List<ScriptValue> list = new ArrayList<>();

                for (JsonElement element : arr) {
                    list.add(context.deserialize(element, ScriptValue.class));
                }

                return new ScriptListValue(list);
            }

            throw new JsonParseException("Unable to convert the json into a script list value!");
        }

        @Override
        public JsonElement serialize(ScriptListValue scriptValue, Type type, JsonSerializationContext context) {
            JsonArray arr = new JsonArray();
            for (ScriptValue val : scriptValue.asList()) {
                arr.add(context.serialize(val));
            }
            return arr;
        }
    }
}
