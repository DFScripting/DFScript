package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CReloadableScreen;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptParametrizedPart;
import io.github.techstreet.dfscript.script.argument.*;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
public class ScriptEditPartScreen extends CReloadableScreen {
    private final Script script;
    private final ScriptParametrizedPart action;
    private final CScrollPanel panel;
    private final List<CWidget> contextMenu = new ArrayList<>();

    public ScriptEditPartScreen(ScriptParametrizedPart action, Script script) {
        super(90, 100);
        panel = new CScrollPanel(0, 0, 90, 100);

        widgets.add(panel);

        this.script = script;
        this.action = action;

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

        panel.add(new CItem(5, 3, action.getIcon()));
        panel.add(new CText(15, 5, Text.of(action.getName())));

        int y = 15;
        int index = 0;
        for (ScriptArgument arg : action.getArguments()) {
            ItemStack icon = arg.getArgIcon();
            String text = arg.getArgText();

            panel.add(new CItem(5, y, icon));
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
                        DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK.value(), 1f,1f));

                        if (button == 0) {
                            ScriptArgument argument = action.getArguments().get(currentIndex);
                            String value = "~";
                            if(argument instanceof ScriptClientValueArgument clientValue) value = clientValue.getName();
                            if(argument instanceof ScriptConfigArgument configArgument) value = configArgument.getName();
                            if(argument instanceof ScriptNumberArgument number) value = String.valueOf(number.value());
                            if(argument instanceof ScriptTextArgument text) value = text.value();
                            if(argument instanceof ScriptVariableArgument var) value = var.name();
                            DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, currentIndex, value));
                        }

                        if (button != 0) {
                            List<ContextMenuButton> contextMenuButtons = new ArrayList<>();
                            contextMenuButtons.add(new ContextMenuButton("Insert Before", () -> {
                                DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, currentIndex));
                            }, false));
                            contextMenuButtons.add(new ContextMenuButton("Insert After", () -> {
                                DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, currentIndex+1));
                            }, false));
                            contextMenuButtons.add(new ContextMenuButton("Delete", () -> {
                                action.getArguments().remove(currentIndex);
                            }));
                            contextMenuButtons.addAll(action.getArguments().get(currentIndex).getContextMenu());
                            DFScript.MC.send(() -> {
                                if(DFScript.MC.currentScreen instanceof ScriptEditPartScreen screen) {
                                    screen.contextMenu((int) x, (int) y, contextMenuButtons);
                                }
                            });
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
            DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, action.getArguments().size()));
        });
        panel.add(add);
    }
}
