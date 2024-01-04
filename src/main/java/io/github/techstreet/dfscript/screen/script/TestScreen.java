package io.github.techstreet.dfscript.screen.script;

import com.google.gson.JsonObject;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.features.AuthHandler;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.script.VirtualScript;
import io.github.techstreet.dfscript.script.util.UploadResponse;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.codec.binary.Base64;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

public class TestScreen extends CScreen {
    public TestScreen() {
        super(160, 160);

        CDragPanel panel = new CDragPanel(0,0,160,160);
        widgets.add(panel);

        CTextField field = new CTextField("", 50, 50, 100, 20, false);

        panel.add(field);
    }
}
