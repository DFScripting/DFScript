package io.github.techstreet.dfscript.script.values;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class ScriptNumberValue extends ScriptValue {

    private final double value;

    public ScriptNumberValue(double value) {
        this.value = value;
    }

    @Override
    String typeName() {
        return "Number";
    }

    @Override
    public ScriptValue convertTo(ScriptValue type) {
        if (type instanceof ScriptDictionaryValue) {
            HashMap<String, ScriptValue> map = new HashMap<>();
            map.put("Number", this);
            return new ScriptDictionaryValue(map);
        } else if (type instanceof ScriptListValue) {
            List<ScriptValue> list = List.of(this);
            return new ScriptListValue(list);
        } else if (type instanceof ScriptBoolValue) {
            return new ScriptBoolValue(value != 0);
        } else {
            return super.convertTo(type);
        }
    }

    @Override
    public double asNumber() {
        return value;
    }

    @Override
    public boolean valueEquals(ScriptValue other) {
        if (!(other.get() instanceof ScriptNumberValue)
            && !(other.get() instanceof ScriptUnknownValue)) {
            return false;
        }
        return value == other.asNumber();
    }

    @Override
    public String asString() {
        if (value % 1 == 0) {
            DecimalFormat df = new DecimalFormat("#");
            df.setRoundingMode(RoundingMode.UNNECESSARY);
            return df.format(value);
        }
        return String.valueOf(value);
    }

    @Override
    public boolean asBoolean() {
        return value != 0;
    }

    @Override
    public int compare(ScriptValue other) {
        if(other instanceof ScriptNumberValue) {
            if(asNumber() == other.asNumber()) {
                return 0;
            }

            if(asNumber() > other.asNumber()) {
                return 1;
            }

            return -1;
        }
        return asString().compareTo(other.asString());
    }

    public static class Serializer implements JsonSerializer<ScriptNumberValue>, JsonDeserializer<ScriptNumberValue> {
        @Override
        public ScriptNumberValue deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            if(jsonElement.isJsonPrimitive()) {
                JsonPrimitive prim = jsonElement.getAsJsonPrimitive();
                if(prim.isNumber()) {
                    return new ScriptNumberValue(prim.getAsDouble());
                }
            }

            throw new JsonParseException("Unable to convert the json into a script number value!");
        }

        @Override
        public JsonElement serialize(ScriptNumberValue scriptValue, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(scriptValue.asNumber());
        }
    }
}
