package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.screen.widget.CWidget;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.action.ScriptAction;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptClientValueArgument;
import io.github.techstreet.dfscript.script.argument.ScriptNumberArgument;
import io.github.techstreet.dfscript.script.argument.ScriptTextArgument;
import io.github.techstreet.dfscript.script.argument.ScriptVariableArgument;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class ScriptEditActionScreen extends CScreen {

    private final Script script;
    private final List<CWidget> contextMenu = new ArrayList<>();

    public ScriptEditActionScreen(ScriptAction action, Script script) {
        super(90, 100);
        this.script = script;

        int y = 5;
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
                    text = String.valueOf((int) na.value());
                } else {
                    text = String.valueOf(na.value());
                }
            } else if (arg instanceof ScriptVariableArgument va) {
                icon = new ItemStack(Items.MAGMA_CREAM);
                text = va.name();
            } else if (arg instanceof ScriptClientValueArgument cva) {
                icon = new ItemStack(Items.NAME_TAG);
                text = cva.getName();
            } else {
                throw new IllegalArgumentException("Invalid argument type");
            }

            widgets.add(new CItem(5, y, icon));
            widgets.add(new CText(15, y + 2, Text.literal(text)));

            int currentIndex = index;


            widgets.add(new CButton(5, y-1, 85, 10, "",() -> {}) {
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
                        DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK, 1f,1f));

                        if (button != 0) {
                            CButton insertBefore = new CButton((int) x, (int) y, 40, 8, "Insert Before", () -> {
                                DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, currentIndex));
                            });
                            CButton insertAfter = new CButton((int) x, (int) y+8, 40, 8, "Insert After", () -> {
                                DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, currentIndex+1));
                            });
                            CButton delete = new CButton((int) x, (int) y + 16, 40, 8, "Delete", () -> {
                                action.getArguments().remove(currentIndex);
                                DFScript.MC.setScreen(new ScriptEditActionScreen(action, script));
                            });
                            DFScript.MC.send(() -> {
                                widgets.add(insertBefore);
                                widgets.add(insertAfter);
                                widgets.add(delete);
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
        widgets.add(add);
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
            widgets.remove(w);
        }
        contextMenu.clear();
    }
}
