package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.action.ScriptAction;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.event.ScriptEvent;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import io.github.techstreet.dfscript.util.render.BlendableTexturedButtonWidget;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ScriptEditScreen extends CScreen {
    private final Identifier identifier_main = new Identifier(DFScript.MOD_ID + ":wrench.png");

    private final Script script;
    private static int scroll = 0;
    private final CScrollPanel panel;
    private final List<CWidget> contextMenu = new ArrayList<>();

    public ScriptEditScreen(Script script) {
        super(125, 100);
        this.script = script;
        panel = new CScrollPanel(0, 3, 120, 94);

        //CTextField description = new CTextField(script.getDescription(), 3, 3, 115, 20, true);
        //description.setChangedListener(() -> script.setDescription(description.getText()));
        //panel.add(description);

        widgets.add(panel);

        int y = 0;
        int index = 0;
        int indent = 0;
        for (ScriptPart part : script.getParts()) {
            if (part instanceof ScriptEvent se) {
                panel.add(new CItem(5, y, se.getType().getIcon()));
                panel.add(new CText(15, y + 2, Text.literal(se.getType().getName())));
                indent = 5;

                int currentIndex = index;
                panel.add(new CButton(5, y-1, 115, 10, "",() -> {}) {
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

                            if (button != 0) {
                                CButton insertBefore = new CButton((int) x, (int) y, 40, 8, "Insert Before", () -> {
                                    DFScript.MC.setScreen(new ScriptActionCategoryScreen(script, currentIndex));
                                });
                                CButton insertAfter = new CButton((int) x, (int) y+8, 40, 8, "Insert After", () -> {
                                    io.github.techstreet.dfscript.DFScript.MC.setScreen(new ScriptActionCategoryScreen(script, currentIndex + 1));
                                });
                                CButton delete = new CButton((int) x, (int) y+16, 40, 8, "Delete", () -> {
                                    script.getParts().remove(currentIndex);
                                    scroll = panel.getScroll();
                                    io.github.techstreet.dfscript.DFScript.MC.setScreen(new ScriptEditScreen(script));
                                });
                                io.github.techstreet.dfscript.DFScript.MC.send(() -> {
                                    panel.add(insertBefore);
                                    panel.add(insertAfter);
                                    panel.add(delete);
                                    contextMenu.add(insertBefore);
                                    contextMenu.add(insertAfter);
                                    contextMenu.add(delete);
                                });
                            }
                            return true;
                        }
                        return false;
                    }
                });
            } else if (part instanceof ScriptAction sa) {
                if (sa.getType() == ScriptActionType.CLOSE_BRACKET) {
                    indent -= 5;
                }

                panel.add(new CItem(5 + indent, y, sa.getType().getIcon()));
                panel.add(new CText(15 + indent, y + 2, Text.literal(sa.getType().getName())));

                for (int i = 0; i < indent; i += 5) {
                    int xpos = 8 + i;
                    int ypos = y;
                    panel.add(new CWidget() {
                        @Override
                        public void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
                            DrawableHelper.fill(stack, xpos, ypos, xpos + 1, ypos + 8, 0xFF333333);
                        }

                        @Override
                        public Rectangle getBounds() {
                            return new Rectangle(0,0,0,0);
                        }
                    });
                }

                int currentIndex = index;
                panel.add(new CButton(5, y-1, 115, 10, "",() -> {}) {
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
                                if (sa.getType() != ScriptActionType.CLOSE_BRACKET) {
                                    scroll = panel.getScroll();
                                    io.github.techstreet.dfscript.DFScript.MC.setScreen(new ScriptEditActionScreen(sa, script));
                                }
                            } else {
                                CButton insertBefore = new CButton((int) x, (int) y, 40, 8, "Insert Before", () -> {
                                    io.github.techstreet.dfscript.DFScript.MC.setScreen(new ScriptActionCategoryScreen(script, currentIndex));
                                });
                                CButton insertAfter = new CButton((int) x, (int) y+8, 40, 8, "Insert After", () -> {
                                    io.github.techstreet.dfscript.DFScript.MC.setScreen(new ScriptActionCategoryScreen(script, currentIndex + 1));
                                });
                                CButton delete = new CButton((int) x, (int) y+16, 40, 8, "Delete", () -> {
                                    script.getParts().remove(currentIndex);
                                    scroll = panel.getScroll();
                                    io.github.techstreet.dfscript.DFScript.MC.setScreen(new ScriptEditScreen(script));
                                });
                                io.github.techstreet.dfscript.DFScript.MC.send(() -> {
                                    panel.add(insertBefore);
                                    panel.add(insertAfter);
                                    panel.add(delete);
                                    contextMenu.add(insertBefore);
                                    contextMenu.add(insertAfter);
                                    contextMenu.add(delete);
                                });
                            }
                            return true;
                        }
                        return false;
                    }
                });

                if (sa.getType().hasChildren()) {
                    indent += 5;
                }
            } else {
                throw new IllegalArgumentException("Unknown script part type");
            }

            y += 10;
            index++;
        }

        CButton add = new CButton(36, y, 46, 8, "Add", () -> {
            DFScript.MC.setScreen(new ScriptActionCategoryScreen(script, script.getParts().size()));
        });

        panel.add(new CButton(84, y, 8, 8, DFScript.MOD_ID + ":wrench.png", () -> {

        }));

        panel.add(add);
        panel.setScroll(scroll);
    }

    @Override
    public void close() {
        scroll = panel.getScroll();
        ScriptManager.getInstance().saveScript(script);
        DFScript.MC.setScreen(new ScriptListScreen());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean b = super.mouseClicked(mouseX, mouseY, button);
        clearContextMenu();
        return b;
    }

    private void clearContextMenu() {
        for (CWidget w : contextMenu) {
            panel.remove(w);
        }
        contextMenu.clear();
    }
}
