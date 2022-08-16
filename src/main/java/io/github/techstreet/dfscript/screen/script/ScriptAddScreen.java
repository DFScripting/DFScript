package io.github.techstreet.dfscript.screen.script;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CPlainPanel;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.script.VirtualScript;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.text.Text;

public class ScriptAddScreen extends CScreen {
    public static ArrayList<VirtualScript> scripts = new ArrayList<>();
    CScrollPanel panel;
    CTextField searchBox;

    public ScriptAddScreen() {
        super(111, 106);
        open();
    }

    private void open() {
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
        ArrayList<VirtualScript> scripts = new ArrayList<>();

        try {
            InputStream is = new URL("https://dfscript-server.techstreetdev.repl.co/scripts/get").openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            JsonObject obj = JsonParser.parseString(readAll(rd)).getAsJsonObject();

            for (String key : obj.keySet()) {
                JsonObject scriptObject = obj.getAsJsonObject(key);
                scripts.add(new VirtualScript(scriptObject.get("name").getAsString(), scriptObject.get("owner").getAsString(), key));
            }

            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ScriptAddScreen.scripts = scripts;
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
            CButton button = new CButton(7, y, 96, 10, script.getName(), () -> {
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
        DFScript.MC.setScreen(new ScriptListScreen());
    }
}