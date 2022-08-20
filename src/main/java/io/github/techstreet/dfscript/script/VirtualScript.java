package io.github.techstreet.dfscript.script;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.script.ScriptListScreen;
import io.github.techstreet.dfscript.screen.script.ScriptMessageScreen;
import io.github.techstreet.dfscript.util.FileUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class VirtualScript {
    private final String name;
    private final String owner;
    private final String id;
    private boolean approved = false;
    private String approver;

    public VirtualScript(String name, String owner, String id) {
        this.name = name;
        this.owner = owner;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getId() {
        return id;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public void download(boolean update) {
        try {
            InputStream is = new URL("https://dfscript-server.techstreetdev.repl.co/scripts/get/" + id).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            File file = FileUtil.folder("Scripts").resolve(name + ".json").toFile();

            if (!update) {
                int count = 1;
                while (file.exists()) {
                    file = FileUtil.folder("Scripts").resolve(name + "_" + count + ".json").toFile();
                    count += 1;
                }
            }

            JsonObject obj = JsonParser.parseReader(rd).getAsJsonObject();
            obj.addProperty("disabled", false);
            obj.addProperty("server", id);
            String content = obj.toString();

            Files.write(file.toPath(), content.getBytes());
            DFScript.MC.setScreen(new ScriptMessageScreen(new ScriptListScreen(true), "Successfully installed '" + name + "' by " + owner + "!"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
