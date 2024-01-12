package io.github.techstreet.dfscript.script.values;

import com.google.gson.*;
import net.minecraft.text.Text;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public abstract class ScriptValue {

    abstract String typeName();

    public String getTypeName() {
        return typeName();
    }

    public String asString() {
        throw new UnsupportedOperationException("Cannot convert " + typeName() + " to string");
    }

    public ScriptTextValue asText() {
        return new ScriptTextValue(asString());
    }

    public double asNumber() {
        throw new UnsupportedOperationException("Cannot convert " + typeName() + " to number");
    }

    public List<ScriptValue> asList() {
        throw new UnsupportedOperationException("Cannot convert " + typeName() + " to list");
    }

    public HashMap<String,ScriptValue> asDictionary() {
        throw new UnsupportedOperationException("Cannot convert " + typeName() + " to dictionary");
    }

    public boolean asBoolean() {
        throw new UnsupportedOperationException("Cannot convert " + typeName() + " to boolean");
    }

    public ScriptValue convertTo(ScriptValue type) {
        throw new UnsupportedOperationException("Cannot convert " + typeName() + " to " + type.getTypeName());
    }

    public ScriptValue get() {
        return this;
    }

    @Override
    public String toString() {
        return asString();
    }

    public abstract boolean valueEquals(ScriptValue other);

    public ScriptValue getCompareValue() {
        return this;
    }

    public int compare(ScriptValue other) {
        return asString().compareTo(other.asString());
    }

    public Text formatAsText() {
        return Text.literal(asString());
    }

    public static class Serializer implements JsonSerializer<ScriptValue>, JsonDeserializer<ScriptValue> {
        @Override
        public ScriptValue deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            if(jsonElement.isJsonNull()) {
                return new ScriptUnknownValue();
            }
            if(jsonElement.isJsonPrimitive()) {
                JsonPrimitive prim = jsonElement.getAsJsonPrimitive();
                if(prim.isNumber()) {
                    return context.deserialize(prim, ScriptNumberValue.class);
                }
                if(prim.isString()) {
                    return context.deserialize(prim, ScriptTextValue.class);
                }
                if(prim.isBoolean()) {
                    return context.deserialize(prim, ScriptBoolValue.class);
                }
            }
            else {
                if(jsonElement.isJsonArray()) {
                    JsonArray array = jsonElement.getAsJsonArray();
                    return context.deserialize(array, ScriptListValue.class);
                }
                if(jsonElement.isJsonObject()) {
                    JsonObject object = jsonElement.getAsJsonObject();

                    if(object.has("___objectType")) {
                        String objectType = object.get("___objectType").getAsString();
                        return switch(objectType) {
                            case "dict" -> context.deserialize(object.get("dict"), ScriptDictionaryValue.class);
                            case "string" -> context.deserialize(object.get("string"), ScriptStringValue.class);
                            default ->
                                throw new JsonParseException("Unable to convert a json object of type '" + objectType + "' into a script value");
                        };
                    } else {
                        return context.deserialize(object, ScriptDictionaryValue.class);
                    }
                }
            }

            throw new JsonParseException("Unable to convert the json into a script value!");
        }

        @Override
        public JsonElement serialize(ScriptValue scriptValue, Type type, JsonSerializationContext context) {
            return JsonNull.INSTANCE;
        }
    }
}
