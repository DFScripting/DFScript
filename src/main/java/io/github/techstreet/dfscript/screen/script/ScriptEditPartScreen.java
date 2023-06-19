package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptParametrizedPart;
import io.github.techstreet.dfscript.script.argument.*;
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

public class ScriptEditPartScreen extends CScreen {

    private final Script script;
    private final CScrollPanel panel;
    private final List<CWidget> contextMenu = new ArrayList<>();

    public ScriptEditPartScreen(ScriptParametrizedPart action, Script script) {
        super(90, 100);
        panel = new CScrollPanel(0, 0, 90, 100);

        widgets.add(panel);

        this.script = script;

        panel.add(new CItem(5, 3, action.getIcon()));
        panel.add(new CText(15, 5, Text.of(action.getName())));

        int y = 15;
        int index = 0;
        for (ScriptArgument arg : action.getArguments()) {
            ItemStack icon;
            String text;
            if (arg instanceof ScriptTextArgument ta) {
                icon = new ItemStack(Items.BOOK);
                text = ta.value();
            } else if (arg instanceof ScriptNumberArgument na) {
                icon = new ItemStack(Items.SLIME_BALL);
                if (na.value() % 1 == 0) {
                    DecimalFormat df = new DecimalFormat("#");
                    df.setRoundingMode(RoundingMode.UNNECESSARY);
                    text = df.format(na.value());
                } else {
                    text = String.valueOf(na.value());
                }
            } else if (arg instanceof ScriptVariableArgument va) {
                icon = new ItemStack(Items.MAGMA_CREAM);
                text = va.name();
            } else if (arg instanceof ScriptClientValueArgument cva) {
                icon = new ItemStack(Items.NAME_TAG);
                text = cva.getName();
            } else if (arg instanceof ScriptConfigArgument ca) {
                icon = new ItemStack(Items.INK_SAC);
                text = ca.getOption().getFullName();
            } else {
                throw new IllegalArgumentException("Invalid argument type");
            }

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
                            CButton insertBefore = new CButton((int) x, (int) y, 40, 8, "Insert Before", () -> {
                                DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, currentIndex));
                            });
                            CButton insertAfter = new CButton((int) x, (int) y+8, 40, 8, "Insert After", () -> {
                                DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, currentIndex+1));
                            });
                            CButton delete = new CButton((int) x, (int) y + 16, 40, 8, "Delete", () -> {
                                action.getArguments().remove(currentIndex);
                                DFScript.MC.setScreen(new ScriptEditPartScreen(action, script));
                            });
                            DFScript.MC.send(() -> {
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

            y += 10;
            index++;

        }

        CButton add = new CButton(25, y, 40, 8, "Add", () -> {
            DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, action.getArguments().size()));
        });
        panel.add(add);
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
}
