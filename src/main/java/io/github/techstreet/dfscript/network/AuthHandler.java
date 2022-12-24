package io.github.techstreet.dfscript.network;

import com.google.gson.JsonObject;
import com.mojang.authlib.exceptions.AuthenticationException;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.loader.Loadable;
import io.github.techstreet.dfscript.network.request.ForbiddenException;
import io.github.techstreet.dfscript.network.request.ReadBody;
import io.github.techstreet.dfscript.network.request.ServerCodeResponse;
import net.minecraft.client.util.Session;
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
    public static AuthHandler instance;

    private String commonSecret;
    protected boolean valid = false;

    @Override
    public void load() {
        try {
            regen();
            instance = this;
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
        // STEP 2: Connect to the fake server, and get our code validated
        try {
            validateCode();
            this.valid = true;
        } catch (AuthenticationException | ForbiddenException e) {
            this.valid = false;
            DFScript.LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sends a code to the server, and generates a hash of this code and one returned by the server.
     * This code is to be used as the bearer token, it just has to be verified by connecting to the fake server.
     * This also sends our username, which is used later for verification.
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
        jsonObject.addProperty("name",DFScript.MC.getSession().getUsername());

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        ServerCodeResponse servercodeResponse = DFScript.GSON.fromJson(ReadBody.getResponse(connection.getInputStream()), ServerCodeResponse.class);
        return DigestUtils.sha256Hex(clientCode + servercodeResponse.getServerCode());

    }

    /**
     * Joins a fake minecraft server, and notifies the backend.
     * The server ID is the common hash, which is then verified as safe to use.
     * If this completes, the common hash should be used as an authentication token.
     * @throws AuthenticationException Likely logged out
     * @throws IOException DFOnline Backend error
     */
    private void validateCode() throws AuthenticationException, IOException, ForbiddenException {

        Session session = DFScript.MC.getSession();
        DFScript.MC.getSessionService().joinServer(session.getProfile(), session.getAccessToken(), commonSecret.substring(0,30));

        URL url = new URL(DFScript.BACKEND + "/user/verify/" + commonSecret);
        DFScript.LOGGER.info(url.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setReadTimeout(500000000);
        connection.setConnectTimeout(500000000);

        int status = connection.getResponseCode();

        if(status == 403) {
            throw new ForbiddenException("Player is banned by the backend!");
        }
        if(status != 204) {
            throw new IOException("Unexpected response code from backend: " + status);
        }
        // It worked.
    }

    public void addAuthorization(HttpURLConnection connection) {
        connection.addRequestProperty("Authorization",commonSecret);
    }
}
