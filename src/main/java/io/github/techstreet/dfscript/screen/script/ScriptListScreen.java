package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.screen.widget.CWidget;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.codec.binary.Base64;

public class ScriptListScreen extends CScreen {
    private final List<CWidget> contextMenu = new ArrayList<>();

    public ScriptListScreen() {
        super(90, 100);
        CScrollPanel panel = new CScrollPanel(0, 5, 120, 94);
        widgets.add(panel);

        int y = 0;
        for (Script s : ScriptManager.getInstance().getScripts()) {
            MutableText text = Text.literal(s.getName());
            if (s.disabled()) {
                text = text.formatted(Formatting.GRAY);
            }
            panel.add(new CText(6, y + 2, text));

            panel.add(new CButton(3, y-1, 82, 10, "",() -> {}) {
                @Override
                public void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
                    Rectangle b = getBounds();
                    if (b.contains(mouseX, mouseY)) {
                        DrawableHelper.fill(stack, b.x, b.y, b.x + b.width, b.y + b.height, 0x33000000);
                    }
                }

                @Override
                public boolean mouseClicked(double x, double y, int button) {
                    if (getBounds().contains(x, y)) {
                        io.github.techstreet.dfscript.DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK, 1f,1f));

                        if (button == 0) {
                            DFScript.MC.setScreen(new ScriptEditScreen(s));
                        } else {
                            CButton delete = new CButton((int) x, (int) y, 40, 8, "Delete", () -> {
                                DFScript.MC.setScreen(new ScriptDeletionScreen(s));
                            });

                            CButton enableDisable;
                            if (s.disabled()) {
                                enableDisable = new CButton((int) x, (int) y + 8, 40, 8, "Enable", () -> {
                                    s.setDisabled(false);
                                    ScriptManager.getInstance().saveScript(s);
                                    DFScript.MC.setScreen(new ScriptListScreen());
                                });
                            } else {
                                enableDisable = new CButton((int) x, (int) y + 8, 40, 8, "Disable", () -> {
                                    s.setDisabled(true);
                                    ScriptManager.getInstance().saveScript(s);
                                    DFScript.MC.setScreen(new ScriptListScreen());
                                });
                            }

                            CButton upload = new CButton((int) x, (int) y + 16, 40, 8, "Upload", () -> {
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
                                    String jsonInputString = "{\"data\": \"" + scriptData + "\"}";
                                    try (OutputStream os = con.getOutputStream()) {
                                        byte[] input = jsonInputString.getBytes("utf-8");
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
                                    }
                                } catch (Exception e) {
                                    ChatUtil.sendMessage("Failed to upload script to the server, please report this to a DFScript developer!", ChatType.FAIL);
                                    e.printStackTrace();
                                }
                            });

                            DFScript.MC.send(() -> {
                                widgets.add(delete);
                                widgets.add(enableDisable);
                                contextMenu.add(delete);
                                contextMenu.add(enableDisable);
                            });

                            if (Objects.equals(s.getServer(), "None")) {
                                DFScript.MC.send(() -> {
                                    widgets.add(upload);
                                    contextMenu.add(upload);
                                });
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });

            y += 12;
        }

        CButton add = new CButton(25, y, 40, 8, "Add", () -> {
            io.github.techstreet.dfscript.DFScript.MC.setScreen(new ScriptAddScreen());
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
