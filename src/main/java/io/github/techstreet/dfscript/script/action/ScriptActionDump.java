package io.github.techstreet.dfscript.script.action;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class ScriptActionDump {
    public static void main(String[] args) {
        JsonObject obj = new JsonObject();

        for (ScriptActionType actionType : ScriptActionType.values()) {
            JsonObject subObj = new JsonObject();
            subObj.addProperty("name", actionType.getName());
            subObj.addProperty("description", String.join("\n", actionType.getDescription()));
            subObj.addProperty("category", actionType.getCategory().name());
            obj.add(actionType.name(), subObj);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.print(gson.toJson(obj));
    }
}
