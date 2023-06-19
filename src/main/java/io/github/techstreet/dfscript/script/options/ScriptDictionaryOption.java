package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.script.ScriptSettingsScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.util.ScriptOptionSubtypeMismatchException;
import io.github.techstreet.dfscript.script.values.ScriptDictionaryValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ScriptDictionaryOption implements ScriptOption {

    List<ScriptDualOption> value = new ArrayList<>();
    ScriptOptionEnum[] valueTypes;

    public ScriptDictionaryOption(JsonElement value, ScriptOptionEnum type1, ScriptOptionEnum type2) throws ScriptOptionSubtypeMismatchException {
        valueTypes = new ScriptOptionEnum[]{type1,type2};

        if(!value.isJsonArray())
        {
            DFScript.LOGGER.error("Not a JSON Array!");
        }

        for(JsonElement e : value.getAsJsonArray())
        {
            this.value.add((ScriptDualOption) ScriptOption.fromJson(e, ScriptOptionEnum.DUAL,
                        List.of(type1, type2)
                    ));
        }

        checkValidity();
    }

    public ScriptDictionaryOption(ScriptOptionEnum type1, ScriptOptionEnum type2) throws ScriptOptionSubtypeMismatchException {
        valueTypes = new ScriptOptionEnum[]{type1,type2};

        checkValidity();
    }

    private void checkValidity() throws ScriptOptionSubtypeMismatchException {
        for(int i = 0; i < 2; i++)
        {
            if(valueTypes[i].getExtraTypes() != 0)
            {
                throw new ScriptOptionSubtypeMismatchException("Incorrect amount of extra types");
            }
        }

        for(ScriptDualOption o : value)
        {
            for(int i = 0; i < 2; i++) {
                if (valueTypes[i] != o.getSubtypes().get(i)) {
                    throw new ScriptOptionSubtypeMismatchException("Incorrect type of an item");
                }
            }
        }
    }

    @Override
    public ScriptValue getValue() {
        HashMap<String,ScriptValue> result = new HashMap<>();

        for(ScriptOption o : value)
        {
            List<ScriptValue> s = o.getValue().asList();

            result.put(s.get(0).asText(),s.get(1));
        }

        return new ScriptDictionaryValue(result);
    }

    @Override
    public boolean convertableTo(ScriptActionArgument.ScriptActionArgumentType arg) {
        return ScriptActionArgument.ScriptActionArgumentType.DICTIONARY.convertableTo(arg);
    }

    @Override
    public int create(CScrollPanel panel, int x, int y, int width) {
        int i = 0;
        for(ScriptOption o : value) {
            int y1 = y;
            y = o.create(panel,x+5,y,width-5);
            int finalI = i;
            panel.add(new CButton(5, y1, 115, y-y1, "",() -> {}) {
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
                            DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK.value(), 1f,1f));
                            if (DFScript.MC.currentScreen instanceof ScriptSettingsScreen s) {
                                CButton insertBefore = new CButton((int) x, (int) y, 50, 8, "Insert Item Before", () -> {
                                    try {
                                        value.add(finalI, (ScriptDualOption) ScriptOption.instantiate(ScriptOptionEnum.DUAL, List.of(valueTypes[0],valueTypes[1])));
                                    } catch (Exception e) {
                                        ChatUtil.error(String.valueOf(e.getCause()));
                                    }
                                    s.reloadMenu();
                                });
                                CButton insertAfter = new CButton((int) x, (int) y + 8, 50, 8, "Insert Item After", () -> {
                                    try {
                                        value.add(finalI + 1, (ScriptDualOption) ScriptOption.instantiate(ScriptOptionEnum.DUAL, List.of(valueTypes[0],valueTypes[1])));
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

        CButton button = new CButton(x+5, y, width-5, 8, "Add Item", ()->{
            try {
                value.add((ScriptDualOption) ScriptOption.instantiate(ScriptOptionEnum.DUAL, List.of(valueTypes[0],valueTypes[1])));
            } catch (Exception e) {
                ChatUtil.error(String.valueOf(e.getCause()));
            }

            if(DFScript.MC.currentScreen instanceof ScriptSettingsScreen s) {
                s.reloadMenu();
            }
        });

        panel.add(button);

        return y+10;
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
        return Arrays.stream(valueTypes).toList();
    }
}
