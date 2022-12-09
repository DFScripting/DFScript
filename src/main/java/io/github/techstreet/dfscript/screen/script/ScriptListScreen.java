package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.script.VirtualScript;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScriptListScreen extends CScreen {
    private final List<CWidget> contextMenu = new ArrayList<>();

    public ScriptListScreen(boolean allowEditAndUpload) {
        super(160, 100);
        CScrollPanel panel = new CScrollPanel(0, 5, 160, 94);
        widgets.add(panel);

        CText test = new CText(0,0,Text.literal("Debug"));
        widgets.add(test);

        int y = 0;
        for (Script s : ScriptManager.getInstance().getScripts()) {
            MutableText text = Text.literal(s.getName());
            VirtualScript script = ScriptAddScreen.scriptHash.get(s.getServer());

            if (script != null) {
                text = Text.literal((script.isApproved() ? "⭐ " : "") + s.getName());

                if (script.isApproved()) {
                    text = text.formatted(Formatting.YELLOW);
                }
            }

            if (s.disabled()) {
                text = text.formatted(Formatting.STRIKETHROUGH);
            }

            panel.add(new CText(6, y + 2, text));

            panel.add(new CButton(4, y-1, 153, 10, "",() -> {}) {
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

            int addedY = 0;
            int addedX = 118;

            if (allowEditAndUpload) {
                // Delete Button
                CButton delete = new CTexturedButton(20 + addedX, y + addedY, 8, 8, DFScript.MOD_ID + ":delete.png", () -> {
                    DFScript.MC.setScreen(new ScriptDeletionScreen(s));
                }, 0, 0, 1, 0.5f, 0, 0.5f);

                if (!Objects.equals(s.getServer(), "None") && s.getOwner() != null && s.getOwner().equals(DFScript.PLAYER_UUID)) {
                    delete.setOnClick(() -> {
                        DFScript.MC.setScreen(new ScriptMessageScreen(new ScriptListScreen(allowEditAndUpload), "That script must be removed from the server to delete it!!"));
                    });
                }

                panel.add(delete);
            }

            // Enable or Disable Button
            CButton enableDisable;
            if (s.disabled()) {
                enableDisable = new CTexturedButton(30 + addedX, y + addedY, 8, 8, DFScript.MOD_ID + ":enable.png", () -> {
                    s.setDisabled(false);
                    ScriptManager.getInstance().saveScript(s);
                    DFScript.MC.setScreen(new ScriptListScreen(allowEditAndUpload));
                }, 0,0,1,0.5f,0,0.5f);
            } else {
                enableDisable = new CTexturedButton(30 + addedX, y + addedY, 8, 8, DFScript.MOD_ID + ":disable.png", () -> {
                    s.setDisabled(true);
                    ScriptManager.getInstance().saveScript(s);
                    DFScript.MC.setScreen(new ScriptListScreen(allowEditAndUpload));
                }, 0,0,1,0.5f,0,0.5f);
            }

            panel.add(enableDisable);

            if(allowEditAndUpload) {
                if (s.getOwner() != null && s.getOwner().equals(DFScript.PLAYER_UUID)) {
                    // Edit Button
                    CButton edit = new CTexturedButton(addedX, y + addedY, 8, 8, DFScript.MOD_ID + ":wrench.png", () -> {
                        DFScript.MC.setScreen(new ScriptEditScreen(s));
                    }, 0, 0, 1, 0.5f, 0, 0.5f);

                    panel.add(edit);

                    // Upload or Remove Button
                    CButton upload = new CTexturedButton(10 + addedX, y + addedY, 8, 8, DFScript.MOD_ID + ":upload.png", () -> {
                        //TODO: replace this with new network manager
//                        try {
//                            // Encode the script JSON to GZIP Base64
//                            byte[] bytes = Files.readAllBytes(s.getFile().toPath());
//
//                            ByteArrayOutputStream rstBao = new ByteArrayOutputStream();
//                            GZIPOutputStream zos = new GZIPOutputStream(rstBao);
//                            zos.write(bytes);
//                            zos.close();
//
//                            String scriptData = Base64.encodeBase64String(rstBao.toByteArray());
//
//                            // Upload the script to the server
//                            URL url = new URL("https://DFScript-Server.techstreetdev.repl.co/scripts/upload");
//                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                            con.setRequestMethod("POST");
//                            con.setRequestProperty("Content-Type", "application/json");
//                            con.setRequestProperty("Accept", "application/json");
//                            con.setRequestProperty("authorization", AuthHandler.getAuthCode());
//                            con.setDoOutput(true);
//
//                            JsonObject obj = new JsonObject();
//                            obj.addProperty("data", scriptData);
//
//                            try (OutputStream os = con.getOutputStream()) {
//                                byte[] input = obj.toString().getBytes(StandardCharsets.UTF_8);
//                                os.write(input, 0, input.length);
//                            }
//
//                            // Parse the response and get the scripts ID
//                            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
//                                StringBuilder response = new StringBuilder();
//                                String responseLine;
//
//                                while ((responseLine = br.readLine()) != null) {
//                                    response.append(responseLine.trim());
//                                }
//
//                                UploadResponse uploadResponse = DFScript.GSON.fromJson(response.toString(), UploadResponse.class);
//                                System.out.println(uploadResponse.getId());
//                                s.setServer(uploadResponse.getId());
//
//                                ScriptManager.getInstance().saveScript(s);
//                                DFScript.MC.setScreen(new ScriptMessageScreen(new ScriptListScreen(allowEditAndUpload), "Successfully uploaded the script to the server!"));
//                            }
//                        } catch (Exception e) {
//                            DFScript.MC.setScreen(new ScriptMessageScreen(new ScriptListScreen(allowEditAndUpload), "Failed to upload script to the server, please report this to a DFScript developer!"));
//                            e.printStackTrace();
//                        }
                    }, 0, 0, 1, 0.5f, 0, 0.5f);

                    if (!Objects.equals(s.getServer(), "None")) {
                        upload = new CTexturedButton(10 + addedX, y + addedY, 8, 8, DFScript.MOD_ID + ":unupload.png", () -> {
                            //TODO: replace this with new network manager
//                            try {
//                                // Remove the script to the server
//                                URL url = new URL("https://DFScript-Server.techstreetdev.repl.co/scripts/remove/");
//                                HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                                con.setRequestMethod("POST");
//                                con.setRequestProperty("Content-Type", "application/json");
//                                con.setRequestProperty("Accept", "application/json");
//                                con.setRequestProperty("authorization", AuthHandler.getAuthCode());
//                                con.setDoOutput(true);
//
//                                JsonObject obj = new JsonObject();
//                                obj.addProperty("id", s.getServer());
//
//                                try (OutputStream os = con.getOutputStream()) {
//                                    byte[] input = obj.toString().getBytes(StandardCharsets.UTF_8);
//                                    os.write(input, 0, input.length);
//                                }
//
//                                try {
//                                    try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
//                                        s.setServer("None");
//                                        ScriptManager.getInstance().saveScript(s);
//                                        DFScript.MC.setScreen(new ScriptMessageScreen(new ScriptListScreen(allowEditAndUpload), "Successfully removed the script from the server!"));
//                                    }
//                                } catch (IOException e) {
//                                    if (e.getMessage().contains("401")) {
//                                        DFScript.MC.setScreen(new ScriptMessageScreen(new ScriptListScreen(allowEditAndUpload), "You don't have permission to delete this script!"));
//                                    } else {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            } catch (Exception e) {
//                                DFScript.MC.setScreen(new ScriptMessageScreen(new ScriptListScreen(allowEditAndUpload), "Failed to remove the script from the server, please try again!"));
//                                e.printStackTrace();
//                            }
                        }, 0, 0, 1, 0.5f, 0, 0.5f);
                    }

                    panel.add(upload);
                }
                else {
                    //Script Settings Button
                    CButton settings = new CTexturedButton(10 + addedX, y + addedY, 8, 8, DFScript.MOD_ID + ":settings.png", () -> {
                        DFScript.MC.setScreen(new ScriptSettingsScreen(s, false));
                    }, 0, 0, 1, 0.5f, 0, 0.5f);

                    panel.add(settings);
                }
            }

            y += 12;
        }

        if (allowEditAndUpload) {
            CButton add = new CButton(60, y + 1, 40, 8, "Add", () -> {
                DFScript.MC.setScreen(new ScriptAddScreen());
            });

            panel.add(add);
        }
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
