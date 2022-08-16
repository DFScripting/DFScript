package io.github.techstreet.dfscript.screen.script;

import com.google.gson.JsonObject;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.features.AuthHandler;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

import io.github.techstreet.dfscript.script.util.UploadResponse;
import io.github.techstreet.dfscript.util.chat.ChatType;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.codec.binary.Base64;

public class ScriptListScreen extends CScreen {
    private final List<CWidget> contextMenu = new ArrayList<>();

    public ScriptListScreen() {
        super(160, 100);
        CScrollPanel panel = new CScrollPanel(0, 5, 160, 94);
        widgets.add(panel);

        int y = 0;
        for (Script s : ScriptManager.getInstance().getScripts()) {
            MutableText text = Text.literal(s.getName());
            if (s.disabled()) {
                text = text.formatted(Formatting.GRAY);
            }
            panel.add(new CText(6, y + 2, text));

            panel.add(new CButton(3, y-1, 152, 10, "",() -> {}) {
                @Override
                public void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
                    Rectangle b = getBounds();
                    DrawableHelper.fill(stack, b.x, b.y, b.x + b.width, b.y + b.height, 0x33000000);
                }

                @Override
                public boolean mouseClicked(double x, double y, int button) {
                    return false;
                }
            });

            int addedY = 5;
            int addedX = 100;

            // Delete Button
            CButton delete = new CTexturedButton(addedX, y + addedY, 8, 8, DFScript.MOD_ID + ":delete.png", () -> {
                DFScript.MC.setScreen(new ScriptDeletionScreen(s));
            }, 0,0,1,0.5f,0,0.5f);

            widgets.add(delete);

            // Enable or Disable Button
            CButton enableDisable;
            if (s.disabled()) {
                enableDisable = new CTexturedButton(10 + addedX, y + addedY, 8, 8, DFScript.MOD_ID + ":enable.png", () -> {
                    s.setDisabled(false);
                    ScriptManager.getInstance().saveScript(s);
                    DFScript.MC.setScreen(new ScriptListScreen());
                }, 0,0,1,0.5f,0,0.5f);
            } else {
                enableDisable = new CTexturedButton(10 + addedX, y + addedY, 8, 8, DFScript.MOD_ID + ":disable.png", () -> {
                    s.setDisabled(true);
                    ScriptManager.getInstance().saveScript(s);
                    DFScript.MC.setScreen(new ScriptListScreen());
                }, 0,0,1,0.5f,0,0.5f);
            }

            widgets.add(enableDisable);

            if (DFScript.MC.player != null && Objects.equals(s.getOwner(), DFScript.MC.player.getUuid().toString())) {
                // Edit Button
                CButton edit = new CTexturedButton(20 + addedX, y + addedY, 8, 8, DFScript.MOD_ID + ":edit.png", () -> {
                    DFScript.MC.setScreen(new ScriptDeletionScreen(s));
                }, 0,0,1,0.5f,0,0.5f);

                widgets.add(edit);

                // Upload or Remove Button
                CButton upload = new CTexturedButton(30 + addedX, y + addedY, 8, 8, DFScript.MOD_ID + ":upload.png", () -> {
                    try {
                        // Encode the script JSON to GZIP Base64
                        byte[] bytes = Files.readAllBytes(s.getFile().toPath());

                        ByteArrayOutputStream rstBao = new ByteArrayOutputStream();
                        GZIPOutputStream zos = new GZIPOutputStream(rstBao);
                        zos.write(bytes);
                        zos.close();

                        String scriptData = Base64.encodeBase64String(rstBao.toByteArray());

                        // Upload the script to the server
                        URL url = new URL("https://DFScript-Server.techstreetdev.repl.co/scripts/upload");
                        HttpURLConnection con = (HttpURLConnection)url.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Content-Type", "application/json");
                        con.setRequestProperty("Accept", "application/json");
                        con.setDoOutput(true);

                        System.out.println(scriptData);

                        JsonObject obj = new JsonObject();
                        obj.addProperty("data", scriptData);
                        obj.addProperty("authcode", AuthHandler.getAuthCode());

                        try (OutputStream os = con.getOutputStream()) {
                            byte[] input = obj.toString().getBytes("utf-8");
                            os.write(input, 0, input.length);
                        }

                        // Parse the response and get the scripts ID
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                            StringBuilder response = new StringBuilder();
                            String responseLine;

                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }

                            UploadResponse uploadResponse = DFScript.GSON.fromJson(response.toString(), UploadResponse.class);
                            System.out.println(uploadResponse.getId());
                            s.setServer(uploadResponse.getId());

                            ScriptManager.getInstance().saveScript(s);
                            DFScript.MC.setScreen(new ScriptListScreen());
                        }
                    } catch (Exception e) {
                        ChatUtil.sendMessage("Failed to upload script to the server, please report this to a DFScript developer!", ChatType.FAIL);
                        e.printStackTrace();
                    }
                }, 0,0,1,0.5f,0,0.5f);

                if (!Objects.equals(s.getServer(), "None")) {
                    upload = new CTexturedButton(30 + addedX, y + addedY, 8, 8, DFScript.MOD_ID + ":remove.png", () -> {
                        try {
                            // Remove the script to the server
                            URL url = new URL("https://DFScript-Server.techstreetdev.repl.co/scripts/remove/" + s.getServer());
                            HttpURLConnection con = (HttpURLConnection)url.openConnection();
                            con.setDoOutput(true);
                        } catch (Exception e) {
                            ChatUtil.sendMessage("Failed to remove script from the server, please report this to a DFScript developer!", ChatType.FAIL);
                            e.printStackTrace();
                        }
                    }, 0,0,1,0.5f,0,0.5f);
                }

                widgets.add(upload);
            }

            y += 12;
        }

        CButton add = new CButton(60, y + 1, 40, 8, "Add", () -> {
            DFScript.MC.setScreen(new ScriptAddScreen());
        });

        panel.add(add);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean b = super.mouseClicked(mouseX, mouseY, button);
        clearContextMenu();
        return b;
    }

    private void clearContextMenu() {
        for (CWidget w : contextMenu) {
            widgets.remove(w);
        }
        contextMenu.clear();
    }
}
