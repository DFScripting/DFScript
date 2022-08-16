package io.github.techstreet.dfscript.script;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.techstreet.dfscript.util.FileUtil;
import io.github.techstreet.dfscript.util.chat.ChatUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

import static io.github.techstreet.dfscript.DFScript.GSON;
import static io.github.techstreet.dfscript.screen.script.ScriptAddScreen.readAll;

public class VirtualScript {
    private final String name;
    private final String owner;
    private final String id;

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

            String content = readAll(rd)
                    .replace("\n", "")
                    .replace("\"disabled\":true", "\"disabled\":false")
                    .replace("\"server\":None", "\"server\":" + id + "");

            System.out.println(content);
            Files.write(file.toPath(), content.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
