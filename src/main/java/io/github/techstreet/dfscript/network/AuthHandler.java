package io.github.techstreet.dfscript.network;

import com.google.gson.JsonObject;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.loader.Loadable;
import io.github.techstreet.dfscript.network.request.ServerCodeResponse;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AuthHandler implements Loadable {
    protected String commonSecret;
    protected boolean valid = false;

    @Override
    public void load() {

        try {
            regen();
        }
        catch (IOException e) {
            DFScript.LOGGER.error(e);
            e.printStackTrace();
        }
    }

    public void regen() throws IOException {
        // STEP 1: Create a client code, and get the server code
        try {
            this.commonSecret = getCommonSecret();
        } catch (IOException e) {
            DFScript.LOGGER.error(e);
            e.printStackTrace();
            return;
        }
        try {
            validateCode();
            this.valid = true;
        } catch (Exception e) {
            DFScript.LOGGER.error(e);
            e.printStackTrace();
            this.valid = false;
        }
    }

    /**
     * Sends a code to the server, and generates a hash of this code and one returned by the server.
     * This code is to be used as the bearer token, it just has to be verified by connecting to the fake server.
     * @return The common code hash
     * @throws IOException Any error in the networking
     */
    private String getCommonSecret() throws IOException {
        URL url = new URL(DFScript.BACKEND + "/user/secret/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        connection.setReadTimeout(5000);
        connection.setConnectTimeout(5000);

        String clientCode = UUID.randomUUID().toString();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("client",clientCode);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;

            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            ServerCodeResponse servercodeResponse = DFScript.GSON.fromJson(response.toString(), ServerCodeResponse.class);
            return DigestUtils.sha256Hex(servercodeResponse.getServerCode() + clientCode);
        }
    }

    private void validateCode() {
        // TODO: validate code.
    }
}
