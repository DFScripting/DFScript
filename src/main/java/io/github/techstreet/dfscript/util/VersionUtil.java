package io.github.techstreet.dfscript.util;

import com.google.gson.JsonObject;
import io.github.techstreet.dfscript.DFScript;

import java.io.IOException;

public class VersionUtil {
    public static int getLatestVersion() {
        try {
            String webContent = WebUtil.getString("https://api.github.com/repos/TechStreetDev/DFScript/releases/latest");
            JsonObject jsonObject = io.github.techstreet.dfscript.DFScript.JSON_PARSER.parse(webContent).getAsJsonObject();
            return Integer.parseInt(jsonObject.get("name").getAsString().substring(6));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getCurrentVersionInt() {
        try {
            return Integer.parseInt(DFScript.MOD_VERSION);
        }catch (NumberFormatException e) {
            return -1;
        }
    }

}