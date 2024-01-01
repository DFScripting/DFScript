package io.github.techstreet.dfscript.script.util;

import com.google.gson.*;
import io.github.techstreet.dfscript.script.values.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ScriptValueJson {

    public static JsonElement toJson(ScriptValue value) {
        if (value instanceof ScriptNumberValue snv) {
            return new JsonPrimitive(snv.asNumber());
        } else if (value instanceof ScriptTextValue stv) {
            return new JsonPrimitive(stv.asString());
        } else if (value instanceof ScriptStringValue ssv) {
            JsonObject inner = new JsonObject();
            inner.add("value", new JsonPrimitive(ssv.toString()));
            JsonObject outer = new JsonObject();
            outer.add("str_el", inner);
            return outer;
        } else if (value instanceof ScriptBoolValue sbv) {
            return new JsonPrimitive(sbv.asBoolean());
        }else if (value instanceof ScriptListValue slv) {
            JsonArray array = new JsonArray();
            for (ScriptValue sv : slv.asList()) {
                array.add(toJson(sv));
            }
            return array;
        } else if (value instanceof ScriptDictionaryValue sdv) {
            JsonObject object = new JsonObject();
            for (String key : sdv.asDictionary().keySet()) {
                object.add(key, toJson(sdv.asDictionary().get(key)));
            }
            return object;
        } else {
            return JsonNull.INSTANCE;
        }
    }

    public static ScriptValue fromJson(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                return new ScriptNumberValue(primitive.getAsNumber().doubleValue());
            } else if (primitive.isString()) {
                return new ScriptTextValue(primitive.getAsString());
            } else if (primitive.isBoolean()) {
                return new ScriptBoolValue(primitive.getAsBoolean());
            } else {
                return new ScriptUnknownValue();
            }
        } else if (element.isJsonArray()) {
            ArrayList<ScriptValue> list = new ArrayList<>();
            for (JsonElement e : element.getAsJsonArray()) {
                list.add(fromJson(e));
            }
            return new ScriptListValue(list);
        } else if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            if (jsonObject.has("str_el")) {
                if (jsonObject.get("str_el").isJsonObject()) {
                    if (jsonObject.get("str_el").getAsJsonObject().has("value")) {
                        return new ScriptStringValue(jsonObject.get("str").getAsJsonObject().get("value").getAsString());
                    }
                }
            }
            HashMap<String, ScriptValue> map = new HashMap<>();
            for (String key : jsonObject.keySet()) {
                map.put(key, fromJson(jsonObject.get(key)));
            }
            return new ScriptDictionaryValue(map);
        } else {
            return new ScriptUnknownValue();
        }
    }

}
