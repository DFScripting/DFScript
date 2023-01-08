package io.github.techstreet.dfscript.network.request;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.script.VirtualScript;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetScripts {
    public static ArrayList<VirtualScript> get() throws IOException {
        ArrayList<VirtualScript> scripts = new ArrayList();
        URL url = new URL(DFScript.BACKEND + "/script");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        JsonObject json = DFScript.GSON.fromJson(ReadBody.getResponse(connection.getInputStream()), JsonObject.class);
        json.asMap().forEach((s, jsonElement) -> {
            JsonObject object = jsonElement.getAsJsonObject();
            VirtualScript script = new VirtualScript(object.get("name").getAsString(), object.get("owner").getAsString(), s);
            JsonElement verified = object.get("verified");
            if(!verified.isJsonNull()) {
                script.setApproved(true);
                script.setApprover(verified.getAsString());
            }
            scripts.add(script);
        });
        return scripts;
    }

    public static VirtualScript get(String id) throws IOException {
        URL url = new URL(DFScript.BACKEND + "/script/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        JsonObject json = DFScript.GSON.fromJson(ReadBody.getResponse(connection.getInputStream()), JsonObject.class);
        VirtualScript script = new VirtualScript(json.get("name").getAsString(), json.get("owner").getAsString(), id);
        JsonElement verified = json.get("verified");
        if(!verified.isJsonNull()) {
            script.setApproved(true);
            script.setApprover(verified.getAsString());
        }
        return script;
    }
}
