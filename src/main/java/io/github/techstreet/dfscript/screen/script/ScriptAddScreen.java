package io.github.techstreet.dfscript.screen.script;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.features.AuthHandler;
import io.github.techstreet.dfscript.network.request.GetScripts;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CPlainPanel;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.script.VirtualScript;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ScriptAddScreen extends CScreen {
    public static ArrayList<VirtualScript> scripts = new ArrayList<>();
    public static HashMap<String, VirtualScript> scriptHash = new HashMap<>();

    CScrollPanel panel;
    CTextField searchBox;

    public ScriptAddScreen() {
        super(111, 106);
        open();
    }

    private void open() {
        getScripts();

        CPlainPanel root = new CPlainPanel(0, 0, 111, 106);

        panel = new CScrollPanel(0, 17, 111, 84);

        root.add(panel);

        searchBox = new CTextField("Search...", 5, 5, 100, 10, true);
        searchBox.setChangedListener(this::update);
        root.add(searchBox);

        widgets.add(root);

        update();
    }

    private void update() {
        List<VirtualScript> filtered = new ArrayList<>();
        String query = searchBox.getText().toLowerCase();

        for (VirtualScript script : scripts) {
            if (!searchBox.getText().equals("Search...")) {
                if (script.getName().toLowerCase().contains(query.toLowerCase()) || script.getOwner().toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(script);
                }
            } else {
                filtered.add(script);
            }
        }

        panel.clear();
        fillPanel(filtered);
    }

    public static void getScripts() {
        try {
            ScriptAddScreen.scripts = GetScripts.get();
            ScriptManager.lastServerUpdate = System.currentTimeMillis() / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;

        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }

        return sb.toString();
    }

    private void fillPanel(List<VirtualScript> scriptList) {
        int y = 18;
        CButton newButton = new CButton(7, 6, 96, 10, "New Script", () -> {
            DFScript.MC.setScreen(new ScriptCreationScreen());
        });

        panel.add(newButton);

        for (VirtualScript script : scriptList) {
            CButton button = new CButton(7, y, 96, 10, (script.isApproved() ? "§e⭐ " : "") + "§f" + script.getName(), () -> {
                DFScript.MC.setScreen(new ScriptInstallScreen(script));
            });

            panel.add(button);
            y += 12;
        }

        long time = Math.abs((System.currentTimeMillis() / 1000) - ScriptManager.lastServerUpdate);
        long minutes = (time % 3600) / 60;
        long seconds = time % 60;

        panel.add(new CText(8, 0, Text.literal("Last update: " + minutes + "m " + seconds + "s" + " ago!")));
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptListScreen(true));
    }
}