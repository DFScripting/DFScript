package io.github.techstreet.dfscript.features;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.loader.Loadable;
import io.github.techstreet.dfscript.script.util.AuthcodeResponse;
import io.github.techstreet.dfscript.script.util.ServercodeResponse;
import io.github.techstreet.dfscript.util.chat.ChatType;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.minecraft.client.util.Session;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class AuthHandler implements Loadable {
    private static String authCode = null;

    @Override
    public void load() {
        URL url;
        HttpURLConnection con;
        String commonSecret;
        JsonObject obj;

        try {
            // Authorization step one - Create a random clientcode
            url = new URL("https://DFScript-Server.techstreetdev.repl.co/auth/secret/");
            con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            String clientCode = UUID.randomUUID().toString();

            obj = new JsonObject();
            obj.addProperty("uuid", DFScript.PLAYER_UUID);
            obj.addProperty("clientcode", clientCode);

            System.out.println(obj);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = obj.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;

                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                ServercodeResponse servercodeResponse = DFScript.GSON.fromJson(response.toString(), ServercodeResponse.class);
                commonSecret = servercodeResponse.getServercode();
            }

            // Authorization step two - Generate the authcode
            url = new URL("https://DFScript-Server.techstreetdev.repl.co/auth/secret/");
            con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            obj = new JsonObject();
            obj.addProperty("secret", commonSecret);
            obj.addProperty("uuid", DFScript.PLAYER_UUID);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = obj.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;

                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                AuthcodeResponse authcodeResponse = DFScript.GSON.fromJson(response.toString(), AuthcodeResponse.class);
                authCode = authcodeResponse.getAuthcode();
                DFScript.LOGGER.info("Authorization code successfully generated: " + authCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAuthCode() {
        return authCode;
    }
}
