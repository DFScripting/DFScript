package io.github.techstreet.dfscript.script.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.techstreet.dfscript.script.values.ScriptDictionaryValue;
import io.github.techstreet.dfscript.script.values.ScriptListValue;
import io.github.techstreet.dfscript.script.values.ScriptNumberValue;
import io.github.techstreet.dfscript.script.values.ScriptTextValue;
import io.github.techstreet.dfscript.script.values.ScriptUnknownValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import java.util.ArrayList;
import java.util.HashMap;

public class ScriptValueJson {

    public static JsonElement toJson(ScriptValue value) {
        if (value instanceof ScriptNumberValue snv) {
            return new JsonPrimitive(snv.asNumber());
        } else if (value instanceof ScriptTextValue stv) {
            return new JsonPrimitive(stv.asText());
        } else if (value instanceof ScriptListValue slv) {
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
            HashMap<String, ScriptValue> map = new HashMap<>();
            for (String key : element.getAsJsonObject().keySet()) {
                map.put(key, fromJson(element.getAsJsonObject().get(key)));
            }
            return new ScriptDictionaryValue(map);
        } else {
            return new ScriptUnknownValue();
        }
    }

}
