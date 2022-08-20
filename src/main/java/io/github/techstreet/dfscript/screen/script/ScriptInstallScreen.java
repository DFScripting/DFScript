package io.github.techstreet.dfscript.screen.script;

import com.google.gson.JsonObject;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.features.AuthHandler;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.script.VirtualScript;
import io.github.techstreet.dfscript.script.util.UploadResponse;
import net.minecraft.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ScriptInstallScreen extends CScreen {

    protected ScriptInstallScreen(VirtualScript script) {
        super(125, 52);

        CText name = new CText(5, 5, Text.literal("Name: " + script.getName()));
        CText owner = new CText(5, 12, Text.literal("Creator: " + script.getOwner()));
        CText id = new CText(5, 19, Text.literal("ID: " + script.getId()));
        CText approved = new CText(5, 26, Text.literal("Approved: " + (script.isApproved() ? "§a✓ by " + script.getApprover() : "§c❌")));

        widgets.add(name);
        widgets.add(owner);
        widgets.add(id);
        widgets.add(approved);

        if (DFScript.MC.player != null) {
            for (Script s : ScriptManager.getInstance().getScripts()) {
                if (AuthHandler.getStaffMember()) {
                    if (!script.isApproved()) {
                        widgets.add(new CButton(38, 38, 40, 10, "Approve", () -> {
                            setApproved(script, true);
                        }));
                    } else {
                        widgets.add(new CButton(38, 38, 40, 10, "Unapprove", () -> {
                            setApproved(script, false);
                        }));
                    }
                }

                if (s.getServer() != null && s.getServer().contains(script.getId())) {
                    if (s.getOwner().contains(script.getOwner())) {
                        return;
                    }

                    widgets.add(new CButton(80, 38, 40, 10, "Update", () -> {
                        script.download(true);
                    }));

                    return;
                }
            }

            widgets.add(new CButton(80, 38, 40, 10, "Install", () -> {
                script.download(false);
            }));
        }
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptAddScreen());
    }

    public static void setApproved(VirtualScript script, boolean approved) {
        try {
            URL url = new URL("https://DFScript-Server.techstreetdev.repl.co/scripts/approve/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("authorization", AuthHandler.getAuthCode());
            con.setDoOutput(true);
            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);

            JsonObject obj = new JsonObject();
            obj.addProperty("id", script.getId());
            obj.addProperty("approved", approved);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = obj.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    DFScript.MC.setScreen(new ScriptMessageScreen(new ScriptInstallScreen(script), "Successfully " + (!approved ? "un" : "") + "approved that script!"));
                }
            } catch (IOException e) {
                if (e.getMessage().contains("403")) {
                    DFScript.MC.setScreen(new ScriptMessageScreen(new ScriptInstallScreen(script), "You don't have permission to " + (!approved ? "un" : "") + "approve this script!"));
                } else {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
