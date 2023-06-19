package io.github.techstreet.dfscript.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.AuthenticationException;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.loader.Loadable;
import io.github.techstreet.dfscript.script.util.AuthcodeResponse;
import io.github.techstreet.dfscript.script.util.ServercodeResponse;
import net.minecraft.client.util.Session;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static io.github.techstreet.dfscript.screen.script.ScriptAddScreen.readAll;

public class AuthHandler implements Loadable {
    private static String authCode = null;
    private static boolean staff = false;

    @Override
    public void load() {
        regen();
    }

    public static void regen() {
        URL url;
        HttpURLConnection con;
        String commonSecret;
        JsonObject obj;

        try {
            // Authorization step one - Create a random clientcode
            url = new URL("https://DFScript-Server.techstreetdev.repl.co/auth/secret/");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);

            String clientCode = UUID.randomUUID().toString();

            obj = new JsonObject();
            obj.addProperty("uuid", DFScript.PLAYER_UUID);
            obj.addProperty("clientcode", clientCode);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = obj.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;

                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                ServercodeResponse servercodeResponse = DFScript.GSON.fromJson(response.toString(), ServercodeResponse.class);
                commonSecret = DigestUtils.sha256Hex(servercodeResponse.getServercode() + clientCode);
                commonSecret = commonSecret.substring(0, 30);
            }

            // Authorization step two - Fake server connect
            try {
                Session session = DFScript.MC.getSession();
                DFScript.MC.getSessionService().joinServer(session.getProfile(), session.getAccessToken(), commonSecret);
            } catch (AuthenticationException e) {
//                DFScript.LOGGER.error(e.getMessage());
//                e.printStackTrace();
            }

            // Authorization step two - Generate the authcode
            url = new URL("https://DFScript-Server.techstreetdev.repl.co/auth/auth/");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);

            obj = new JsonObject();
            obj.addProperty("secret", commonSecret);
            obj.addProperty("uuid", DFScript.PLAYER_UUID);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = obj.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;

                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                AuthcodeResponse authcodeResponse = DFScript.GSON.fromJson(response.toString(), AuthcodeResponse.class);
                authCode = authcodeResponse.getAuthcode();
                DFScript.LOGGER.info("Server authorization code successfully generated!");
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }

        try {
            URLConnection con2 = new URL("https://dfscript-server.techstreetdev.repl.co/staff/").openConnection();
            con2.setReadTimeout(5000);
            con2.setConnectTimeout(5000);
            InputStream is = con2.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            obj = JsonParser.parseString(readAll(rd)).getAsJsonObject();

            JsonArray array = obj.get("staff").getAsJsonArray();
            boolean localStaff = false;

            for (JsonElement staffMember : array) {
                if (Objects.equals(staffMember.getAsString(), DFScript.PLAYER_UUID)) {
                    staff = true;
                    localStaff = true;
                }
            }

            if (!localStaff) {
                staff = false;
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public static String getAuthCode() {
        return authCode;
    }

    public static boolean getStaffMember() {
        return staff;
    }
}
