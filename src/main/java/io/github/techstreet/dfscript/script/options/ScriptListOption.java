package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.script.ScriptSettingsScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.util.ScriptOptionSubtypeMismatchException;
import io.github.techstreet.dfscript.script.values.ScriptListValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScriptListOption implements ScriptOption {

    List<ScriptOption> value = new ArrayList<>();
    ScriptOptionEnum valueType = null;

    public ScriptListOption(JsonElement value, ScriptOptionEnum valueType) throws ScriptOptionSubtypeMismatchException {
        this.valueType = valueType;

        if (!value.isJsonArray()) {
            DFScript.LOGGER.error("Not a JSON Array!");
        }

        for (JsonElement e : value.getAsJsonArray()) {
            this.value.add(ScriptOption.fromJson(e, valueType, new ArrayList<>()));
        }

        checkValidity();
    }

    public ScriptListOption(ScriptOptionEnum valueType) throws ScriptOptionSubtypeMismatchException {
        this.valueType = valueType;

        checkValidity();
    }

    private void checkValidity() throws ScriptOptionSubtypeMismatchException {
        if (valueType.getExtraTypes() != 0) {
            throw new ScriptOptionSubtypeMismatchException("Incorrect amount of extra types");
        }

        for (ScriptOption o : value) {
            if (valueType.getOptionType() != o.getClass()) {
                throw new ScriptOptionSubtypeMismatchException("Incorrect type of an item");
            }
        }
    }

    @Override
    public ScriptValue getValue() {
        List<ScriptValue> result = new ArrayList<>();

        for (ScriptOption o : value) {
            result.add(o.getValue());
        }

        return new ScriptListValue(result);
    }

    @Override
    public boolean convertableTo(ScriptActionArgument.ScriptActionArgumentType arg) {
        return ScriptActionArgument.ScriptActionArgumentType.LIST.convertableTo(arg);
    }

    @Override
    public int create(CScrollPanel panel, int x, int y, int width) {
        int i = 0;
        for (ScriptOption o : value) {
            int y1 = y;
            y = o.create(panel, x + 5, y, width - 5);
            int finalI = i;
            panel.add(new CButton(5, y1, 115, y - y1, "", () -> {
            }) {
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
                        if (button != 0) {
                            DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK.value(), 1f, 1f));
                            if (DFScript.MC.currentScreen instanceof ScriptSettingsScreen s) {
                                CButton insertBefore = new CButton((int) x, (int) y, 50, 8, "Insert Item Before", () -> {
                                    try {
                                        value.add(finalI, valueType.getOptionType().getConstructor().newInstance());
                                    } catch (Exception e) {
                                        ChatUtil.error(String.valueOf(e.getCause()));
                                    }
                                    s.reloadMenu();
                                });
                                CButton insertAfter = new CButton((int) x, (int) y + 8, 50, 8, "Insert Item After", () -> {
                                    try {
                                        value.add(finalI + 1, valueType.getOptionType().getConstructor().newInstance());
                                    } catch (Exception e) {
                                        ChatUtil.error(String.valueOf(e.getCause()));
                                    }
                                    s.reloadMenu();
                                });
                                CButton delete = new CButton((int) x, (int) y + 16, 50, 8, "Delete Item", () -> {
                                    value.remove(finalI);
                                    s.reloadMenu();
                                });
                                s.newContextMenu(new CButton[]{insertBefore, insertAfter, delete});
                            }
                            return true;
                        }
                    }
                    return false;
                }
            });
            i++;
        }

        CButton button = new CButton(x + 5, y, width - 5, 8, "Add Item", () -> {
            try {
                value.add(valueType.getOptionType().getConstructor().newInstance());
            } catch (Exception e) {
                ChatUtil.error(String.valueOf(e.getCause()));
            }

            if (DFScript.MC.currentScreen instanceof ScriptSettingsScreen s) {
                s.reloadMenu();
            }
        });

        panel.add(button);

        return y + 10;
    }

    @Override
    public JsonElement getJsonElement() {
        JsonArray array = new JsonArray(value.size());
        for (ScriptOption o : value) {
            array.add(o.getJsonElement());
        }

        return array;
    }

    @Override
    public List<ScriptOptionEnum> getSubtypes() {
        return Arrays.stream(new ScriptOptionEnum[]{valueType}).toList();
    }
}
