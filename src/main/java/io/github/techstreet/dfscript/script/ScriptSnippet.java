package io.github.techstreet.dfscript.script;

import com.google.gson.*;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.screen.script.ScriptEditPartScreen;
import io.github.techstreet.dfscript.screen.script.ScriptEditScreen;
import io.github.techstreet.dfscript.screen.script.ScriptPartCategoryScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CWidget;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.action.ScriptBuiltinAction;
import io.github.techstreet.dfscript.script.conditions.ScriptBranch;
import io.github.techstreet.dfscript.script.conditions.ScriptBuiltinCondition;
import io.github.techstreet.dfscript.script.conditions.ScriptConditionType;
import io.github.techstreet.dfscript.script.event.ScriptEvent;
import io.github.techstreet.dfscript.script.event.ScriptEventType;
import io.github.techstreet.dfscript.script.event.ScriptHeader;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.repetitions.ScriptBuiltinRepetition;
import io.github.techstreet.dfscript.script.repetitions.ScriptRepetitionType;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

import javax.naming.Context;
import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScriptSnippet extends ArrayList<ScriptPart> {
    ScriptSnippet() {

    }

    public void run(ScriptTask task, ScriptScopeParent parent)
    {
        task.stack().push(this, parent);
    }

    public int create(CScrollPanel panel, int y, int indent, Script script) {
        int index = 0;
        ScriptSnippet thisSnippet = this;
        for(ScriptPart part : this) {
            ScriptPartRender render = new ScriptPartRender();
            part.create(render, script);
            y = render.create(panel, y, indent, script);
            int currentIndex = index;
            for (var buttonPos : render.getButtonPositions()) {
                panel.add(new CButton(5, buttonPos.getY() - 1, 115, buttonPos.height(), "", () -> {
                }) {
                    @Override
                    public void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
                        Rectangle b = getBounds();
                        int color = 0;
                        boolean drawFill = false;
                        if (b.contains(mouseX, mouseY)) {
                            drawFill = true;
                            color = 0x33000000;

                            if (part.isDeprecated()) {
                                color = 0x80FF0000;
                            }


                        } else {
                            if (part.isDeprecated()) {
                                drawFill = true;
                                color = 0x33FF0000;
                            }
                        }

                        if(drawFill) {
                            for(var renderButtonPos : render.getButtonPositions()) {
                                DrawableHelper.fill(stack, b.x, renderButtonPos.y()-1, b.x + b.width, renderButtonPos.y()-1 + renderButtonPos.height(), color);
                            }
                        }
                    }

                    @Override
                    public boolean mouseClicked(double x, double y, int button) {
                        if (getBounds().contains(x, y)) {
                            DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK, 1f, 1f));

                            if (button == 0) {
                                if(part instanceof ScriptParametrizedPart parametrizedPart)
                                    DFScript.MC.setScreen(new ScriptEditPartScreen(parametrizedPart, script));
                                if(part instanceof ScriptComment)
                                    return false;
                            } else {
                                List<ContextMenuButton> contextMenu = new ArrayList<>();
                                contextMenu.add(new ContextMenuButton("Insert Before", () -> {
                                    DFScript.MC.setScreen(new ScriptPartCategoryScreen(script, thisSnippet, currentIndex));
                                }, false));
                                contextMenu.add(new ContextMenuButton("Insert After", () -> {
                                    DFScript.MC.setScreen(new ScriptPartCategoryScreen(script, thisSnippet, currentIndex + 1));
                                }, false));
                                contextMenu.add(new ContextMenuButton("Delete", () -> {
                                    thisSnippet.remove(currentIndex);
                                }));
                                contextMenu.addAll(part.getContextMenu());
                                DFScript.MC.send(() -> {
                                    if(DFScript.MC.currentScreen instanceof ScriptEditScreen editScreen)
                                    {
                                        editScreen.contextMenu((int) x, (int) y, contextMenu);
                                    }
                                });
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }
            index++;
        }

        ScriptPartRender.createIndent(panel, indent, y, 8);
        CButton add = new CButton((ScriptEditScreen.width-30)/2, y, 30, 8, "Add Part", () -> {
            DFScript.MC.setScreen(new ScriptPartCategoryScreen(script, thisSnippet, thisSnippet.size()));
        });

        panel.add(add);

        return y+10;
    }

    public void replaceAction(ScriptActionType oldAction, ScriptActionType newAction) {
        for(ScriptPart part : this) {
            if(part instanceof ScriptBuiltinAction a) {
                if(a.getType() == oldAction) {
                    a.setType(newAction);
                }
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.replaceAction(oldAction, newAction));
            }
        }
    }

    public void replaceCondition(ScriptConditionType oldCondition, ScriptConditionType newCondition) {
        for(ScriptPart part : this) {
            if(part instanceof ScriptBranch b) {
                if(b.getCondition() instanceof ScriptBuiltinCondition c) {
                    if(c.getType() == oldCondition) {
                        c.setType(newCondition);
                    }
                }
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.replaceCondition(oldCondition, newCondition));
            }
        }
    }

    public void replaceRepetition(ScriptRepetitionType oldRepetition, ScriptRepetitionType newRepetition) {
        for(ScriptPart part : this) {
            if(part instanceof ScriptBuiltinRepetition r) {
                if(r.getType() == oldRepetition) {
                    r.setType(newRepetition);
                }
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.replaceRepetition(oldRepetition, newRepetition));
            }
        }
    }

    public void updateScriptReferences(Script script) {
        for(ScriptPart part : this) {
            if(part instanceof ScriptParametrizedPart p) {
                p.updateScriptReferences(script);
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.updateScriptReferences(script));
            }
        }
    }

    public void replaceOption(String oldOption, String newOption) {
        for(ScriptPart part : this) {
            if(part instanceof ScriptParametrizedPart p) {
                p.updateConfigArguments(oldOption, newOption);
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.replaceOption(oldOption, newOption));
            }
        }
    }

    public void removeOption(String option) {
        for(ScriptPart part : this) {
            if(part instanceof ScriptParametrizedPart p) {
                p.removeConfigArguments(option);
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.removeOption(option));
            }
        }
    }

    public static class Serializer implements JsonSerializer<ScriptSnippet>, JsonDeserializer<ScriptSnippet> {

        @Override
        public ScriptSnippet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            ScriptSnippet snippet = new ScriptSnippet();

            for(JsonElement element : obj.getAsJsonArray("parts")) {
                snippet.add(context.deserialize(element, ScriptPart.class));
            }

            return snippet;
        }

        @Override
        public JsonElement serialize(ScriptSnippet src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            JsonArray parts = new JsonArray();

            for (ScriptPart part : src) {
                parts.add(context.serialize(part));
            }

            obj.add("parts", parts);
            return obj;
        }
    }
}
