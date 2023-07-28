package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CReloadableScreen;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.screen.util.ItemMaterialSelectMenu;
import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.event.ScriptFunction;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScriptEditFunctionScreen extends CReloadableScreen {

    private final Script script;
    private final ScriptFunction function;
    private final CScrollPanel panel;
    private final List<CWidget> contextMenu = new ArrayList<>();

    public ScriptEditFunctionScreen(ScriptFunction function, Script script) {
        super(90, 100);
        panel = new CScrollPanel(0, 0, 90, 100);

        widgets.add(panel);

        this.script = script;
        this.function = function;

        reload();
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptEditScreen(script));
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

    public void contextMenu(int x, int y, List<ContextMenuButton> contextMenuButtons) {
        clearContextMenu();

        int maxWidth = 0;

        for(ContextMenuButton w : contextMenuButtons)
        {
            TextRenderer t = DFScript.MC.textRenderer;
            int width = t.getWidth(w.getName())/2 + 4;

            if(width > maxWidth) maxWidth = width;
        }

        for(ContextMenuButton w : contextMenuButtons)
        {
            CButton button = new CButton(x, y, maxWidth, 8, w.getName(), w.getOnClick());
            y += 8;

            panel.add(button);
            contextMenu.add(button);
        }
    }

    @Override
    public void reload() {
        clearContextMenu();
        panel.clear();

        CTextField nameField = new CTextField(function.getName(), 15, 3, 90-10-10, 8, true);
        nameField.setMultiline(false);
        nameField.setChangedListener(() -> {
            if(script.functionExists(nameField.getText())) {
                nameField.textColor = 0xFF0000;
            }
            else {
                nameField.textColor = 0xFFFFFF;
                function.setName(nameField.getText());
                script.replaceFunction(function.getName(), nameField.getText());
            }
        });

        CItem icon = new CItem(5, 3, function.getIcon());

        icon.setClickListener((button) -> {
            DFScript.MC.setScreen(new ItemMaterialSelectMenu(function.getRawIcon(), (newIcon) -> {
                function.setIcon(newIcon);
                DFScript.MC.setScreen(new ScriptEditFunctionScreen(function, script));
            }));
        });

        panel.add(icon);
        panel.add(nameField);

        int y = 15;
        int index = 0;
        for (ScriptActionArgument arg : function.argList()) {
            ItemStack argIcon = arg.type().icon();
            String text = arg.name();

            panel.add(new CItem(5, y, argIcon));
            panel.add(new CText(15, y + 2, Text.literal(text)));

            int currentIndex = index;


            panel.add(new CButton(5, y-1, 85, 10, "",() -> {}) {
                @Override
                public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
                    Rectangle b = getBounds();
                    if (b.contains(mouseX, mouseY)) {
                        context.fill(b.x, b.y, b.x + b.width, b.y + b.height, 0x33000000);
                    }
                }

                @Override
                public boolean mouseClicked(double x, double y, int button) {
                    if (getBounds().contains(x, y)) {
                        DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK.value(),
                        1f,1f));

                        if (button != 0) {
                            List<ContextMenuButton> contextMenuButtons = new ArrayList<>();
                            contextMenuButtons.add(new ContextMenuButton("Insert Before", () -> {
                                DFScript.MC.setScreen(new ScriptAddFunctionArgumentScreen(script, function, currentIndex));
                            }, false));
                            contextMenuButtons.add(new ContextMenuButton("Insert After", () -> {
                                DFScript.MC.setScreen(new ScriptAddFunctionArgumentScreen(script, function, currentIndex+1));
                            }, false));
                            contextMenuButtons.add(new ContextMenuButton("Delete", () -> {
                                function.argList().remove(currentIndex);
                            }));
                            DFScript.MC.send(() -> {
                                if(DFScript.MC.currentScreen instanceof ScriptEditFunctionScreen screen) {
                                    screen.contextMenu((int) x, (int) y, contextMenuButtons);
                                }
                            });
                        }
                        else
                        {
                            DFScript.MC.setScreen(new ScriptEditFunctionArgumentScreen(script, function, arg));
                        }

                        return true;
                    }
                    return false;
                }
            });

            y += 10;
            index++;

        }

        CButton add = new CButton(25, y, 40, 8, "Add", () -> {
            DFScript.MC.setScreen(new ScriptAddFunctionArgumentScreen(script, function, function.argList().size()));
        });
        panel.add(add);
    }
}
