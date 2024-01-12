package io.github.techstreet.dfscript.script.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.event.HudRenderEvent;
import io.github.techstreet.dfscript.event.system.CancellableEvent;
import io.github.techstreet.dfscript.screen.overlay.OverlayManager;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.menu.*;
import io.github.techstreet.dfscript.script.repetitions.ScriptRepetition;
import io.github.techstreet.dfscript.script.util.ScriptValueItem;
import io.github.techstreet.dfscript.script.util.ScriptValueJson;
import io.github.techstreet.dfscript.script.values.*;
import io.github.techstreet.dfscript.util.*;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum ScriptActionType {

    /////////////
    /* VISUALS */
    /////////////

    DISPLAY_CHAT(builder -> builder.name("Display Chat")
            .description("Displays a message in the chat.")
            .icon(Items.BOOK)
            .category(ScriptActionCategory.VISUALS)
            .arg("Texts", ScriptActionArgumentType.TEXT, arg -> arg.plural(true))
            .action(ctx -> {
                Component send = Component.empty();

                int i = 0;
                List<ScriptValue> values = ctx.pluralValue("Texts");
                for (ScriptValue arg : values) {
                    send = send.append(arg.formatAsText());
                    if (i < values.size() - 1) { send = send.appendSpace(); }
                    i ++;
                }

                ChatUtil.sendMessage(send);
            })),

    ACTIONBAR(builder -> builder.name("Action Bar")
            .description("Displays a message in the action bar.")
            .icon(Items.SPRUCE_SIGN)
            .category(ScriptActionCategory.VISUALS)
            .arg("Texts", ScriptActionArgumentType.TEXT, arg -> arg.plural(true))
            .action(ctx -> {
                Component send = Component.empty();

                int i = 0;
                List<ScriptValue> values = ctx.pluralValue("Texts");
                for (ScriptValue arg : values) {
                    send = send.append(arg.formatAsText());
                    if (i < values.size() - 1) { send = send.appendSpace(); }
                    i ++;
                }

                ChatUtil.sendActionBar(send);
            })),


    PLAY_SOUND(builder -> builder.name("Play Sound")
            .description("Plays a sound.")
            .icon(Items.NAUTILUS_SHELL)
            .category(ScriptActionCategory.VISUALS)
            .arg("Sound", ScriptActionArgumentType.STRING)
            .arg("Volume", ScriptActionArgumentType.NUMBER, b -> b.optional(true).defaultValue(1))
            .arg("Pitch", ScriptActionArgumentType.NUMBER, b -> b.optional(true).defaultValue(1))
            .action(ctx -> {
                String sound = ctx.value("Sound").asString();
                double volume = 1;
                double pitch = 1;

                if (ctx.argMap().containsKey("Volume")) {
                    volume = ctx.value("Volume").asNumber();
                }

                if (ctx.argMap().containsKey("Pitch")) {
                    pitch = ctx.value("Pitch").asNumber();
                }

                Identifier sndid = null;
                SoundManager sndManager = DFScript.MC.getSoundManager();

                try {
                    sndid = new Identifier(sound);
                } catch (Exception err) {
                    err.printStackTrace();
                    OverlayManager.getInstance().add("Incorrect identifier: " + sound);
                    return;
                }

                if (sndManager.getKeys().contains(sndid)) {
                    SoundEvent snd = SoundEvent.of(sndid);
                    sndManager.play(PositionedSoundInstance.master(snd, (float) pitch, (float) volume));
                } else {
                    OverlayManager.getInstance().add("Unknown sound: " + sound);

                    try {
                        String jname = StringUtil.fromSoundIDToRegistryID(sound);

                        List<String> similiar = new ArrayList<>();

                        int counter = 0;
                        for (Identifier id : sndManager.getKeys()) {
                            String sid = id.toString();
                            String name = StringUtil.fromSoundIDToRegistryID(sid);
                            if (name.contains(jname)) {
                                similiar.add(sid);
                                counter++;
                                if (counter > 5) {
                                    break;
                                }
                            }
                        }

                        if (similiar.size() > 0) {
                            OverlayManager.getInstance().add("Did you mean: \n" + String.join(", \n", similiar));
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            })),

    STOP_ALL_SOUNDS(builder -> builder.name("Stop All Sounds")
            .description("Stops all sounds.")
            .icon(Items.COAL)
            .category(ScriptActionCategory.VISUALS)
            .action(ctx -> DFScript.MC.getSoundManager().stopAll())),


    DISPLAY_TITLE(builder -> builder.name("Display Title")
            .description("Displays a title.")
            .icon(Items.WARPED_SIGN)
            .category(ScriptActionCategory.VISUALS)
            .arg("Title", ScriptActionArgumentType.TEXT)
            .arg("Subtitle", ScriptActionArgumentType.TEXT, b -> b.optional(true).defaultValue(""))
            .arg("Fade In", ScriptActionArgumentType.NUMBER, b -> b.optional(true).defaultValue(20))
            .arg("Stay", ScriptActionArgumentType.NUMBER, b -> b.optional(true).defaultValue(60))
            .arg("Fade Out", ScriptActionArgumentType.NUMBER, b -> b.optional(true).defaultValue(20))
            .action(ctx -> {
                Component title = ctx.value("Title").asText().parse();
                Component subtitle = Component.empty();
                int fadeIn = 20;
                int stay = 60;
                int fadeOut = 20;

                if (ctx.argMap().containsKey("Subtitle")) {
                    subtitle = ctx.value("Subtitle").asText().parse();
                }

                if (ctx.argMap().containsKey("Fade In")) {
                    fadeIn = (int) ctx.value("Fade In").asNumber();
                }

                if (ctx.argMap().containsKey("Stay")) {
                    stay = (int) ctx.value("Stay").asNumber();
                }

                if (ctx.argMap().containsKey("Fade Out")) {
                    fadeOut = (int) ctx.value("Fade Out").asNumber();
                }

                ChatUtil.sendTitle(title, subtitle);
                DFScript.MC.inGameHud.setTitleTicks(fadeIn, stay, fadeOut);
            })),

    DRAW_TEXT(builder -> builder.name("Draw Text")
            .description("Draws text on the screen. (Only works in the overlay event)")
            .icon(Items.NAME_TAG)
            .category(ScriptActionCategory.VISUALS)
            .arg("Text", ScriptActionArgumentType.TEXT)
            .arg("X", ScriptActionArgumentType.NUMBER)
            .arg("Y", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                Text text = ctx.value("Text").formatAsText();
                int x = (int) ctx.value("X").asNumber();
                int y = (int) ctx.value("Y").asNumber();

                if (ctx.task().event() instanceof HudRenderEvent event) {
                    event.context().drawText(DFScript.MC.textRenderer, text, x, y, 0xFFFFFF, true);
                }
            })),

    /////////////
    /* ACTIONS */
    /////////////

    SEND_CHAT(builder -> builder.name("Send Chat")
            .description("Makes the player send a chat message.")
            .icon(Items.PAPER)
            .category(ScriptActionCategory.ACTIONS)
            .arg("Messages", ScriptActionArgumentType.STRING, arg -> arg.plural(true))
            .action(ctx -> {
                StringBuilder sb = new StringBuilder();
                for (ScriptValue arg : ctx.pluralValue("Messages")) {
                    sb.append(arg.asString())
                            .append(" ");
                }

                sb.deleteCharAt(sb.length() - 1);

                if (sb.toString().startsWith("/")) {
                    sb.deleteCharAt(0);

                    Objects.requireNonNull(DFScript.MC.getNetworkHandler()).sendCommand(sb.toString());
                } else {
                    Objects.requireNonNull(DFScript.MC.getNetworkHandler()).sendChatMessage(sb.toString());
                }
            })),


    SET_HOTBAR_ITEM(builder -> builder.name("Set Hotbar Item")
            .description("Sets a hotbar item. (Requires Creative)")
            .icon(Items.IRON_AXE)
            .category(ScriptActionCategory.ACTIONS)
            .arg("Slot", ScriptActionArgumentType.NUMBER)
            .arg("Item", ScriptActionArgumentType.DICTIONARY)
            .action(ctx -> {
                int slot = (int) ctx.value("Slot").asNumber();
                ItemStack item = ScriptValueItem.itemFromValue(ctx.value("Item"));

                if (io.github.techstreet.dfscript.DFScript.MC.interactionManager.getCurrentGameMode() == GameMode.CREATIVE) {
                    io.github.techstreet.dfscript.DFScript.MC.interactionManager.clickCreativeStack(item, slot + 36);
                    io.github.techstreet.dfscript.DFScript.MC.player.getInventory().setStack(slot, item);
                } else {
                    OverlayManager.getInstance().add("Unable to set hotbar item! (Not in creative mode)");
                }
            })),

    GIVE_ITEM(builder -> builder.name("Give Item")
            .description("Gives the player an item. (Requires Creative)")
            .icon(Items.CHEST)
            .category(ScriptActionCategory.ACTIONS)
            .arg("Item", ScriptActionArgumentType.DICTIONARY)
            .action(ctx -> {
                ItemStack item = ScriptValueItem.itemFromValue(ctx.value("Item"));

                if (io.github.techstreet.dfscript.DFScript.MC.interactionManager.getCurrentGameMode() == GameMode.CREATIVE) {
                    ItemUtil.giveCreativeItem(item, true);
                } else {
                    OverlayManager.getInstance().add("Unable to set give item! (Not in creative mode)");
                }
            })),

    //////////
    /* MISC */
    //////////

    GET_REQUEST(builder -> builder.name("GET Web Request")
            .description("Makes a get request to the internet.")
            .icon(Items.GRASS_BLOCK)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("URL", ScriptActionArgumentType.STRING)
            .category(ScriptActionCategory.MISC)
            .action(ctx -> {
                try {
                    StringBuilder result = new StringBuilder();
                    URL url = new URL(ctx.value("URL").asString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        for (String line; (line = reader.readLine()) != null; ) {
                            result.append(line);
                        }
                    }
                    ctx.setVariable("Result", new ScriptTextValue(result.toString()));
                } catch (MalformedURLException ex) {
                    OverlayManager.getInstance().add("The URL is malformed! (" + ctx.value("URL").asString() + ")");
                } catch (IOException ignored) {

                }
            })),


    REGISTER_CMD(builder -> builder.name("Register Command")
            .description("Registers a /cmd completion.")
            .icon(Items.COMMAND_BLOCK)
            .category(ScriptActionCategory.MISC)
            .arg("Commands", ScriptActionArgumentType.STRING, b -> b.plural(true))
            .action(ctx -> {
                for (ScriptValue cmd : ctx.pluralValue("Commands")) {
                    try {
                        String[] args = cmd.asString().split(" ", -1);
                        ArgumentBuilder<FabricClientCommandSource, ?> ab = RequiredArgumentBuilder.argument("args", StringArgumentType.greedyString());

                        ab.executes(ctx2 -> 0);

                        for (int i = args.length - 1; i >= 0; i--) {
                            LiteralArgumentBuilder<FabricClientCommandSource> l = LiteralArgumentBuilder.literal(args[i]);
                            l.then(ab);
                            ab = l;
                        }

                        if (ab instanceof LiteralArgumentBuilder lab) {
                            if (ClientCommandManager.getActiveDispatcher() == null) {
                                ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(lab));
                            } else {
                                ClientCommandManager.getActiveDispatcher().register(lab);
                            }
                        }

                        ClientPlayNetworkHandler nh = DFScript.MC.getNetworkHandler();
                        if (nh != null) {
                            nh.onCommandTree(new CommandTreeS2CPacket(nh.getCommandDispatcher().getRoot()));
                        }
                    } catch (Exception e) {
                        OverlayManager.getInstance().add("Cannot register command '" + cmd.asString() + "': " + e.getMessage());

                        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                            DFScript.LOGGER.error(stackTraceElement.toString());
                        }
                    }
                }
            })),


    READ_FILE(builder -> builder.name("Read File")
            .description("Reads a file from the scripts folder.")
            .icon(Items.WRITTEN_BOOK)
            .category(ScriptActionCategory.MISC)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Filename", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                String filename = ctx.value("Filename").asString();

                if (filename.matches("^[a-zA-Z\\d_\\-. ]+$")) {
                    Path f = FileUtil.folder("Scripts").resolve(ctx.task().context().script().getFile().getName() + "-files").resolve(filename);
                    if (Files.exists(f)) {
                        try {
                            String content = FileUtil.readFile(f);
                            JsonElement json = JsonParser.parseString(content);
                            ScriptValue value = ScriptManager.getInstance().getGSON().fromJson(json, ScriptValue.class);
                            ctx.setVariable("Result", value);
                        } catch (IOException e) {
                            OverlayManager.getInstance().add("Internal error while reading file: " + filename);
                        }
                    }
                } else {
                    OverlayManager.getInstance().add("Illegal filename: " + filename);
                }
            })),

    WRITE_FILE(builder -> builder.name("Write File")
            .description("Writes a file to the scripts folder.")
            .icon(Items.WRITABLE_BOOK)
            .category(ScriptActionCategory.MISC)
            .arg("Filename", ScriptActionArgumentType.STRING)
            .arg("Content", ScriptActionArgumentType.ANY)
            .action(ctx -> {
                String filename = ctx.value("Filename").asString();
                ScriptValue value = ctx.value("Content");

                if (filename.matches("^[a-zA-Z\\d_\\-. ]+$")) {
                    Path f = FileUtil.folder("Scripts").resolve(ctx.task().context().script().getFile().getName() + "-files").resolve(filename);
                    try {
                        f.toFile().getParentFile().mkdirs();
                        FileUtil.writeFile(f, ScriptManager.getInstance().getGSON().toJson(value));
                    } catch (IOException e) {
//                    e.printStackTrace();
                        OverlayManager.getInstance().add("Internal error while writing file: " + filename);
                    }
                } else {
                    OverlayManager.getInstance().add("Illegal filename: " + filename);
                }
            })),

    MESSAGE_BOX(builder -> builder.name("Send Message Box")
            .description("Sends a MessageBox pop up. Returns the option index.\nDoes not work on POJAV Launcher")
            .icon(Items.OAK_SIGN)
            .category(ScriptActionCategory.MISC)
            .arg("Result", ScriptActionArgumentType.NUMBER)
            .arg("Title", ScriptActionArgumentType.STRING)
            .arg("Message", ScriptActionArgumentType.STRING)
            .arg("Options", ScriptActionArgumentType.LIST)
            .action(ctx -> {
                String title = ctx.value("Title").asString();
                String message = ctx.value("Message").asString();
                ArrayList<String> options = new ArrayList<>();
                for (ScriptValue option : ctx.value("Options").asList()) {
                    options.add(option.asString());
                }
                String[] optionsList = options.stream().toList().toArray(new String[0]);
                int selected = PopUpUtil.messageBox(title, message, optionsList);
                ctx.setVariable("Result", new ScriptNumberValue(selected + 1));
            })),

    INPUT_BOX(builder -> builder.name("Send Input Box")
            .description("Sends an Input Box pop up. Returns the input text.\nDoes not work on POJAV Launcher")
            .icon(Items.DAMAGED_ANVIL)
            .category(ScriptActionCategory.MISC)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Title", ScriptActionArgumentType.STRING)
            .arg("Message", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                String title = ctx.value("Title").asString();
                String message = ctx.value("Message").asString();
                String input = PopUpUtil.inputBox(title, message);
                ctx.setVariable("Result", new ScriptStringValue(input));
            })),

    //////////////
    /* VARIABLE */
    //////////////

    SET_VARIABLE(builder -> builder.name("Set Variable")
            .description("Sets a variable to a value.")
            .icon(Items.IRON_INGOT)
            .category(ScriptActionCategory.VARIABLES)
            .arg("Variable", ScriptActionArgumentType.VARIABLE)
            .arg("Value", ScriptActionArgumentType.ANY)
            .action(ctx -> ctx.setVariable(
                    "Variable",
                    ctx.value("Value")
            ))),

    CONVERT_TYPE(builder -> builder.name("Convert Type")
            .description("Converts one type of data to another.\nType can be any value with the data type you want.")
            .icon(Items.END_CRYSTAL)
            .category(ScriptActionCategory.VARIABLES)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Value", ScriptActionArgumentType.ANY)
            .arg("Type", ScriptActionArgumentType.ANY)
            .action(ctx -> {
                ScriptValue type = ctx.value("Type");
                ScriptValue value = ctx.value("Value");
                if (type.getTypeName().equals(value.getTypeName())) {
                    ctx.setVariable("Result", value);
                } else if (type instanceof ScriptUnknownValue) {
                    ctx.setVariable("Result", new ScriptUnknownValue());
                } else if (type instanceof ScriptStringValue) {
                    ctx.setVariable("Result", new ScriptStringValue(value.toString()));
                } else if (type instanceof ScriptTextValue) {
                    ctx.setVariable("Result", new ScriptTextValue(value.toString()));
                } else {
                    ctx.setVariable("Result", value.convertTo(type));
                }
            })),

    PURGE_VAR(builder -> builder.name("Purge Variable")
            .description("Purges all variables with name matching a RegEx.")
            .icon(Items.DEAD_BRAIN_CORAL)
            .category(ScriptActionCategory.VARIABLES)
            .arg("RegEx", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                List<String> vars = new ArrayList<>();
                ctx.task().variables().variables.forEach((key, value) -> {
                    if (key.matches(ctx.value("RegEx").asString())) {
                        vars.add(key);
                    }
                });
                vars.forEach(key -> {
                    ctx.task().variables().variables.remove(key);
                });
            })),

    //////////////
    /* BOOLEANS */
    //////////////


    INVERT(builder -> builder.name("Bitwise NOT")
            .description("Returns true if the boolean is false and vice versa.")
            .icon(Items.REDSTONE_TORCH)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Value", ScriptActionArgumentType.BOOL)
            .category(ScriptActionCategory.BOOLEANS)
            .action(ctx -> {
                ctx.setVariable("Result", new ScriptBoolValue(!ctx.value("Value").asBoolean()));
            })),

    AND(builder -> builder.name("Bitwise AND")
            .description("Returns true if all booleans are true.")
            .icon(Items.REDSTONE_BLOCK)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Value", ScriptActionArgumentType.BOOL, b -> b.plural(true))
            .category(ScriptActionCategory.BOOLEANS)
            .action(ctx -> {
                for (ScriptValue val : ctx.pluralValue("Value")) {
                    if (!val.asBoolean()) {
                        ctx.setVariable("Result", new ScriptBoolValue(false));
                        return;
                    }
                }

                ctx.setVariable("Result", new ScriptBoolValue(true));
            })),

    OR(builder -> builder.name("Bitwise OR")
            .description("Returns true if one of the booleans are true.")
            .icon(Items.REDSTONE)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Value", ScriptActionArgumentType.BOOL, b -> b.plural(true))
            .category(ScriptActionCategory.BOOLEANS)
            .action(ctx -> {
                for (ScriptValue val : ctx.pluralValue("Value")) {
                    if (val.asBoolean()) {
                        ctx.setVariable("Result", new ScriptBoolValue(true));
                        return;
                    }
                }

                ctx.setVariable("Result", new ScriptBoolValue(false));
            })),

    XOR(builder -> builder.name("Bitwise XOR")
            .description("Returns true if an odd number of booleans are true.")
            .icon(Items.REDSTONE, true)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Value", ScriptActionArgumentType.BOOL, b -> b.plural(true))
            .category(ScriptActionCategory.BOOLEANS)
            .action(ctx -> {
                int trues = 0;

                for (ScriptValue val : ctx.pluralValue("Value")) {
                    if (val.asBoolean()) {
                        trues++;
                    }
                }

                ctx.setVariable("Result", new ScriptBoolValue(trues % 2 == 1));
            })),

    /////////////
    /* NUMBERS */
    /////////////

    INCREMENT(builder -> builder.name("Increment")
            .description("Increments a variable by a value.")
            .icon(Items.GLOWSTONE_DUST)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Variable", ScriptActionArgumentType.VARIABLE)
            .arg("Amount", ScriptActionArgumentType.NUMBER, arg -> {
                arg.plural(true);
                arg.optional(true);
                arg.defaultValue(1);
            })
            .action(ctx -> {
                double value = ctx.value("Variable").asNumber();
                for (ScriptValue val : ctx.pluralValue("Amount")) {
                    value += val.asNumber();
                }
                ctx.setVariable(
                        "Variable",
                        new ScriptNumberValue(value)
                );
            })),

    DECREMENT(builder -> builder.name("Decrement")
            .description("Decrements a variable by a value.")
            .icon(Items.REDSTONE)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Variable", ScriptActionArgumentType.VARIABLE)
            .arg("Amount", ScriptActionArgumentType.NUMBER, arg -> {
                arg.plural(true);
                arg.optional(true);
                arg.defaultValue(1);
            })
            .action(ctx -> {
                double value = ctx.value("Variable").asNumber();
                for (ScriptValue val : ctx.pluralValue("Amount")) {
                    value -= val.asNumber();
                }
                ctx.setVariable(
                        "Variable",
                        new ScriptNumberValue(value)
                );
            })),

    ADD(builder -> builder.name("Add")
            .description("Sets a variable to the sum of the number(s).")
            .icon(Items.BRICK)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Numbers", ScriptActionArgumentType.NUMBER, arg -> arg.plural(true))
            .action(ctx -> {
                double value = 0;
                for (ScriptValue val : ctx.pluralValue("Numbers")) {
                    value += val.asNumber();
                }
                ctx.setVariable(
                        "Result",
                        new ScriptNumberValue(value)
                );
            })),

    SUBTRACT(builder -> builder.name("Subtract")
            .description("Sets a variable to the difference of the number(s).")
            .icon(Items.NETHER_BRICK)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Numbers", ScriptActionArgumentType.NUMBER, arg -> arg.plural(true))
            .action(ctx -> {
                double value = ctx.value("Numbers").asNumber();
                boolean first = true;
                for (ScriptValue val : ctx.pluralValue("Numbers")) {
                    if (first) {
                        first = false;
                    } else {
                        value -= val.asNumber();
                    }
                }
                ctx.setVariable(
                        "Result",
                        new ScriptNumberValue(value)
                );
            })),

    MULTIPLY(builder -> builder.name("Multiply")
            .description("Sets a variable to the product of the number(s).")
            .icon(Items.BRICKS)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Numbers", ScriptActionArgumentType.NUMBER, arg -> arg.plural(true))
            .action(ctx -> {
                double value = 1;
                for (ScriptValue val : ctx.pluralValue("Numbers")) {
                    value *= val.asNumber();
                }
                ctx.setVariable(
                        "Result",
                        new ScriptNumberValue(value)
                );
            })),

    DIVIDE(builder -> builder.name("Divide")
            .description("Sets a variable to the quotient of the number(s).")
            .icon(Items.NETHER_BRICKS)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Numbers", ScriptActionArgumentType.NUMBER, arg -> arg.plural(true))
            .action(ctx -> {
                double value = ctx.value("Numbers").asNumber();
                boolean first = true;
                for (ScriptValue val : ctx.pluralValue("Numbers")) {
                    if (first) {
                        first = false;
                    } else {
                        value /= val.asNumber();
                    }
                }
                ctx.setVariable(
                        "Result",
                        new ScriptNumberValue(value)
                );
            })),

    MODULO(builder -> builder.name("Modulo")
            .description("Sets a variable to the remainder of the numbers.")
            .icon(Items.NETHER_WART)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Dividend", ScriptActionArgumentType.NUMBER)
            .arg("Divisor", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                double dividend = ctx.value("Dividend").asNumber();
                double divisor = ctx.value("Divisor").asNumber();
                ctx.setVariable(
                        "Result",
                        new ScriptNumberValue(dividend % divisor)
                );
            })),


    ROUND_NUM(builder -> builder.name("Round Number")
            .description("Rounds a number.")
            .icon(Items.QUARTZ_STAIRS)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Number", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                double number = ctx.value("Number").asNumber();
                ctx.setVariable("Result", new ScriptNumberValue(Math.round(number)));
            })),

    FLOOR_NUM(builder -> builder.name("Floor Number")
            .description("Rounds a number down.")
            .icon(Items.OAK_STAIRS)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Number", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                double number = ctx.value("Number").asNumber();
                ctx.setVariable("Result", new ScriptNumberValue(Math.floor(number)));
            })),

    CEIL_NUM(builder -> builder.name("Ceil Number")
            .description("Rounds a number up.")
            .icon(Items.DARK_OAK_STAIRS)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Number", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                double number = ctx.value("Number").asNumber();
                ctx.setVariable("Result", new ScriptNumberValue(Math.ceil(number)));
            })),


    PARSE_NUMBER(builder -> builder.name("Parse Number")
            .description("Parses a number from a text.")
            .icon(Items.ANVIL)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("String", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                String text = ctx.value("String").asString();
                try {
                    ctx.setVariable("Result", new ScriptNumberValue(Double.parseDouble(text)));
                } catch (NumberFormatException e) {
                    ctx.setVariable("Result", new ScriptUnknownValue());
                }
            })),


    RANDOM_INT(builder -> builder.name("Random Int")
            .description("Generates a random whole number between two other numbers.")
            .icon(Items.HOPPER)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Min", ScriptActionArgumentType.NUMBER)
            .arg("Max", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                int min = (int) ctx.value("Min").asNumber();
                int max = (int) ctx.value("Max").asNumber();
                Random random = new Random();
                int result = random.nextInt(max + 1 - min) + min;
                ctx.setVariable(
                        "Result",
                        new ScriptNumberValue(result)
                );
            })),

    RANDOM_DOUBLE(builder -> builder.name("Random Double")
            .description("Generates a random floating point number between two other numbers.")
            .icon(Items.HOPPER)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Min", ScriptActionArgumentType.NUMBER)
            .arg("Max", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                double min = ctx.value("Min").asNumber();
                double max = ctx.value("Max").asNumber();
                double result = Math.random() * (max - min) + min;
                ctx.setVariable(
                        "Result",
                        new ScriptNumberValue(result)
                );
            })),

    ///////////
    /* LISTS */
    ///////////

    CREATE_LIST(builder -> builder.name("Create List")
            .description("Creates a new list.")
            .icon(Items.ENDER_CHEST)
            .category(ScriptActionCategory.LISTS)
            .arg("Variable", ScriptActionArgumentType.VARIABLE)
            .arg("Values", ScriptActionArgumentType.ANY, b -> b.plural(true)
                    .optional(true))
            .action(ctx -> {
                ArrayList<ScriptValue> values = new ArrayList<>();
                if (ctx.argMap().containsKey("Values")) {
                    for (ScriptArgument v : ctx.argMap().get("Values")) {
                        values.add(v.getValue(ctx.task()));
                    }
                }
                ctx.setVariable("Variable", new ScriptListValue(values));
            })),

    APPEND_VALUE(builder -> builder.name("Append Values")
            .description("Appends values to a list.")
            .icon(Items.FURNACE)
            .category(ScriptActionCategory.LISTS)
            .arg("List", ScriptActionArgumentType.VARIABLE)
            .arg("Values", ScriptActionArgumentType.ANY, b -> b.plural(true))
            .action(ctx -> {
                List<ScriptValue> list = ctx.value("List").asList();
                for (ScriptArgument v : ctx.argMap().get("Values")) {
                    list.add(v.getValue(ctx.task()));
                }
                ctx.setVariable("List", new ScriptListValue(list));
            })),

    APPEND_LIST_VALUES(builder -> builder.name("Append List Values")
            .description("Appends one list's contents to another.")
            .icon(Items.BLAST_FURNACE)
            .category(ScriptActionCategory.LISTS)
            .arg("Base List", ScriptActionArgumentType.VARIABLE)
            .arg("Other List", ScriptActionArgumentType.LIST)
            .action(ctx -> {

                List<ScriptValue> receiver = ctx.value("Receiving List").asList();
                List<ScriptValue> donor = ctx.value("Other List").asList();

                receiver.addAll(donor);

                ctx.setVariable("Receiving List", new ScriptListValue(receiver));
            })),

    GET_LIST_VALUE(builder -> builder.name("Get List Value")
            .description("Gets a value from a list.")
            .icon(Items.BOOK)
            .category(ScriptActionCategory.LISTS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("List", ScriptActionArgumentType.LIST)
            .arg("Index", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                List<ScriptValue> list = ctx.value("List").asList();
                // force index consistent with diamondfire indexes
                int index = (int) ctx.value("Index").asNumber() - 1;
                if (index < 0 || index >= list.size()) {
                    ctx.setVariable("Result", new ScriptUnknownValue());
                } else {
                    ctx.setVariable("Result", list.get(index));
                }
            })),

    GET_VALUE_INDEX(builder -> builder.name("Get List Index of Value")
            .description("Searches for a value in a list variable and gets the index if found.")
            .icon(Items.FLINT)
            .category(ScriptActionCategory.LISTS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("List", ScriptActionArgumentType.LIST)
            .arg("Value", ScriptActionArgumentType.ANY)
            .action(ctx -> {
                List<ScriptValue> list = ctx.value("List").asList();
                ScriptValue value = ctx.value("Value");
                int index = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).valueEquals(value)) {
                        index = i + 1;
                        break;
                    }
                }
                ctx.setVariable("Result", new ScriptNumberValue(index));
            })),

    SET_LIST_VALUE(builder -> builder.name("Set List Value")
            .description("Sets a value in a list.")
            .icon(Items.WRITABLE_BOOK)
            .category(ScriptActionCategory.LISTS)
            .arg("List", ScriptActionArgumentType.VARIABLE)
            .arg("Index", ScriptActionArgumentType.NUMBER)
            .arg("Value", ScriptActionArgumentType.ANY)
            .action(ctx -> {
                List<ScriptValue> list = ctx.value("List").asList();
                // force index consistent with diamondfire indexes
                int index = (int) ctx.value("Index").asNumber() - 1;
                if (index < 0 || index >= list.size()) {
                    return;
                }
                list.set(index, ctx.value("Value"));
                ctx.setVariable("List", new ScriptListValue(list));
            })),

    POP_AT_INDEX(builder -> builder.name("Pop Index in List")
            .description("Pops the index from a list and returns the value.")
            .icon(Items.POPPED_CHORUS_FRUIT)
            .category(ScriptActionCategory.LISTS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("List", ScriptActionArgumentType.VARIABLE)
            .arg("Index", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                List<ScriptValue> list = ctx.value("List").asList();
                int index = (int) ctx.value("Index").asNumber() - 1;
                if (index < 0 || index >= list.size()) {
                    return;
                }
                ScriptValue popped = list.remove(index);
                ctx.setVariable("List", new ScriptListValue(list));
                ctx.setVariable("Result", popped);
            })),

    REMOVE_LIST_AT_INDEX_VALUE(builder -> builder.name("Remove List Value")
            .description("Removes a value from a list.")
            .icon(Items.TNT)
            .category(ScriptActionCategory.LISTS)
            .arg("List", ScriptActionArgumentType.VARIABLE)
            .arg("Index", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                List<ScriptValue> list = ctx.value("List").asList();
                // 1-based indexes
                int index = (int) ctx.value("Index").asNumber() - 1;
                if (index < 0 || index >= list.size()) {
                    return;
                }
                list.remove(index);
                ctx.setVariable("List", new ScriptListValue(list));
            })),

    REMOVE_LIST_VALUE(builder -> builder.name("Remove List Value")
            .description("Removes a value from a list.")
            .icon(Items.TNT_MINECART)
            .category(ScriptActionCategory.LISTS)
            .arg("List", ScriptActionArgumentType.VARIABLE)
            .arg("Value", ScriptActionArgumentType.ANY)
            .action(ctx -> {
                List<ScriptValue> list = ctx.value("List").asList();

                list.removeIf(value -> value.valueEquals(ctx.value("Value")));

                ctx.setVariable("List", new ScriptListValue(list));
            })),

    LIST_LENGTH(builder -> builder.name("List Length")
            .description("Returns the length of a list.")
            .icon(Items.BOOKSHELF)
            .category(ScriptActionCategory.LISTS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("List", ScriptActionArgumentType.LIST)
            .action(ctx -> {
                ctx.setVariable("Result", new ScriptNumberValue(ctx.value("List").asList().size()));
            })),


    SORT_LIST(builder -> builder.name("Sort List")
            .description("Sorts a list in ascending order.")
            .icon(Items.REPEATING_COMMAND_BLOCK)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("List", ScriptActionArgumentType.LIST, b -> b.optional(true))
            .category(ScriptActionCategory.LISTS)
            .action(ctx -> {
                List<ScriptValue> list;

                if (ctx.argMap().containsKey("List")) {
                    list = ctx.value("List").asList();
                } else {
                    list = ctx.value("Result").asList();
                }

                list.sort(new ScriptValueComparator());

                ctx.setVariable("Result", new ScriptListValue(list));
            })),

    ///////////
    /* TEXTS */
    ///////////

    SET_TO_TEXT(builder -> builder.name("Set to Text")
            .description("Sets a variable to a text.")
            .icon(Items.BOOK)
            .category(ScriptActionCategory.TEXTS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Add Spaces", ScriptActionArgumentType.BOOL)
            .arg("Values", ScriptActionArgumentType.ANY, arg -> {
                arg.plural(true);
                arg.optional(true);
            })
            .action(ctx -> {
                boolean addSpaces = ctx.value("Add Spaces").asBoolean();

                String result;

                if (ctx.argMap().containsKey("Values")) {
                    StringBuilder sb = new StringBuilder();
                    for (ScriptValue arg : ctx.pluralValue("Values")) {
                        sb.append(arg.asString());
                        if (addSpaces) {
                            sb.append(" ");
                        }
                    }
                    if (addSpaces) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    result = sb.toString();
                } else {
                    result = "";
                }
                ctx.setVariable("Result", new ScriptStringValue(result));
            })),

    MEASURE_TEXT(builder -> builder.name("Measure Text")
            .description("Measures the width of a text in pixels.")
            .icon(Items.STICK)
            .category(ScriptActionCategory.TEXTS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Text", ScriptActionArgumentType.TEXT)
            .action(ctx -> {
                Text text = ctx.value("Text").formatAsText();;
                int width = DFScript.MC.textRenderer.getWidth(text);
                ctx.setVariable("Result", new ScriptNumberValue(width));
            })),


    STRIP_COLOR(builder -> builder.name("Strip Color from Text")
            .description("Removes all colors from the text.")
            .icon(Items.CYAN_DYE)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Text", ScriptActionArgumentType.TEXT, b -> b.optional(true))
            .category(ScriptActionCategory.TEXTS)
            .action(ctx -> {
                String result;

                if (ctx.argMap().containsKey("Text")) {
                    result = ctx.value("Text").asString();
                } else {
                    result = ctx.value("Result").asString();
                }

                result = result.replaceAll("<color:.*?>", "").replaceAll("</color:.*?>", "");
                result = result.replaceAll("<#\\d+>", "").replaceAll("</#\\d+>", "");
                result = result.replaceAll("<gradient:.*?:.*?>", "").replaceAll("</gradient>", "");
                result = result.replaceAll("<rainbow(:.*?)?>", "").replaceAll("</rainbow>", "");
                String[] colors = {
                        "black",
                        "dark_blue",
                        "dark_green",
                        "dark_aqua",
                        "dark_red",
                        "dark_purple",
                        "gold",
                        "gray",      "grey",
                        "dark_gray", "dark_grey",
                        "blue",
                        "green",
                        "aqua",
                        "red",
                        "light_purple",
                        "yellow",
                        "white"
                };
                for (String c : colors) {
                    result = result.replaceAll("<" + c + ">", "").replaceAll("</" + c + ">", "");
                }

                ctx.setVariable("Result", new ScriptTextValue(result));
            })),

    CLEAR_FORMATTING(builder -> builder.name("Clear Formatting")
            .description("Clears all formatting from the text")
            .icon(Items.GLASS)
            .category(ScriptActionCategory.TEXTS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Text", ScriptActionArgumentType.TEXT)
            .action(ctx -> {
                Component parsed = ctx.value("Text").asText().parse();
                String plain = PlainTextComponentSerializer.plainText().serialize(parsed);
                ctx.setVariable("Result", new ScriptStringValue(plain));
            })),

    GET_MINIMESSAGE(builder -> builder.name("Get MiniMessage Expression")
            .description("Gets the expression for a text.")
            .icon(Items.STRING)
            .category(ScriptActionCategory.TEXTS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Text", ScriptActionArgumentType.TEXT)
            .action(ctx -> {
                String c = ctx.value("Text").asText().asString();
                ctx.setVariable("Result", new ScriptStringValue(c));
            })),

    PARSE_MINIMESSAGE(builder -> builder.name("Parse MiniMessage Expression")
            .description("Parses a MiniMessage expression into a text.")
            .icon(Items.FILLED_MAP)
            .category(ScriptActionCategory.TEXTS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("String", ScriptActionArgumentType.STRING)
            .action(ctx -> ctx.setVariable("Result", new ScriptTextValue(ctx.value("String").asString())))),

    GET_TEXT_LENGTH(builder -> builder.name("Get Text Content Length")
            .description("Get the length of the text content.")
            .icon(Items.CHISELED_BOOKSHELF)
            .category(ScriptActionCategory.TEXTS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Text", ScriptActionArgumentType.TEXT)
            .action(ctx -> {
                Component parsed = ctx.value("Text").asText().parse();
                String plain = PlainTextComponentSerializer.plainText().serialize(parsed);
                ctx.setVariable("Result", new ScriptNumberValue(plain.length()));
            })),

    /////////////
    /* STRINGS */
    /////////////

    JOIN_STRING(builder -> builder.name("Join String")
            .description("Joins multiple strings into one.")
            .icon(Items.STRING)
            .category(ScriptActionCategory.STRINGS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Strings", ScriptActionArgumentType.STRING, arg -> arg.plural(true))
            .action(ctx -> {
                StringBuilder sb = new StringBuilder();
                for (ScriptValue arg : ctx.pluralValue("Strings")) {
                    sb.append(arg.asString());
                }
                ctx.setVariable(
                        "Result",
                        new ScriptStringValue(sb.toString())
                );
            })),


    COPY_STRING(builder -> builder.name("Copy String")
            .description("Copies the string to the clipboard.")
            .icon(Items.PAPER)
            .category(ScriptActionCategory.STRINGS)
            .arg("String", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                DFScript.MC.keyboard.setClipboard(ctx.value("String").asString());
            })),

    SPLIT_STRING(builder -> builder.name("Split String")
            .description("Splits a strings into a list of strings.")
            .icon(Items.SHEARS)
            .category(ScriptActionCategory.STRINGS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("String", ScriptActionArgumentType.STRING)
            .arg("Separator", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                String text = ctx.value("String").asString();
                String separator = ctx.value("Separator").asString();
                List<ScriptValue> split = new ArrayList<>();

                for (String s : text.split(Pattern.quote(separator))) {
                    split.add(new ScriptStringValue(s));
                }

                ctx.setVariable("Result", new ScriptListValue(split));
            })),

    REGEX_SPLIT_STRING(builder -> builder.name("Split String by Regex")
            .description("Splits a string into a list of strings\nusing a regex as a separator.")
            .icon(Items.SHEARS, true)
            .category(ScriptActionCategory.STRINGS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("String", ScriptActionArgumentType.STRING)
            .arg("Separator (Regex)", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                String text = ctx.value("String").asString();
                String separator = ctx.value("Separator (Regex)").asString();
                List<ScriptValue> split = new ArrayList<>();

                for (String s : text.split(separator)) {
                    split.add(new ScriptStringValue(s));
                }

                ctx.setVariable("Result", new ScriptListValue(split));
            })),


    JOIN_LIST_TO_STRING(builder -> builder.name("Join List to String")
            .description("Joins a list into a single string.")
            .icon(Items.SLIME_BALL)
            .category(ScriptActionCategory.STRINGS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("List", ScriptActionArgumentType.LIST)
            .arg("Separator", ScriptActionArgumentType.STRING, b -> b.optional(true).defaultValue(", "))
            .action(ctx -> {
                String separator = ", ";

                if (ctx.argMap().containsKey("Separator")) {
                    separator = ctx.value("Separator").asString();
                }

                String result = ctx.value("List")
                        .asList().stream()
                        .map(ScriptValue::asString)
                        .collect(Collectors.joining(separator));

                ctx.setVariable("Result", new ScriptStringValue(result));
            })),

    STRING_INDEX_OF(builder -> builder.name("Index Of String")
            .description("Gets the index of the first occurrence of a string within another string.")
            .icon(Items.FLINT)
            .category(ScriptActionCategory.STRINGS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("String", ScriptActionArgumentType.STRING)
            .arg("Sub-String", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                int result = ctx.value("String").asString().indexOf(ctx.value("Sub-String").asString()) + 1;
                ctx.setVariable("Result", new ScriptNumberValue(result));
            })),

    GET_SUBSTRING(builder -> builder.name("Get Sub-String")
            .description("Gets a piece of string within another string.")
            .icon(Items.SADDLE)
            .category(ScriptActionCategory.STRINGS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("String", ScriptActionArgumentType.STRING)
            .arg("First Index", ScriptActionArgumentType.NUMBER)
            .arg("Last Index", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                String string = ctx.value("String").asString();
                int start = (int) ctx.value("First Index").asNumber() - 1;
                int end = (int) ctx.value("Last Index").asNumber();
                String result = string.substring(start, end);
                ctx.setVariable("Result", new ScriptStringValue(result));
            })),

    STRING_LENGTH(builder -> builder.name("Get String Length")
            .description("Get the length of a string value.")
            .icon(Items.BOOKSHELF)
            .category(ScriptActionCategory.STRINGS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("String", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                String text = ctx.value("String").asString();
                ctx.setVariable("Result", new ScriptNumberValue(text.length()));
            })),


    REPLACE_STRING(builder -> builder.name("Replace String")
            .description("Searches for part of a string and replaces it.")
            .icon(Items.LEAD)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("String to change", ScriptActionArgumentType.STRING)
            .arg("String part to replace", ScriptActionArgumentType.STRING)
            .arg("Replacement", ScriptActionArgumentType.STRING)
            .category(ScriptActionCategory.STRINGS)
            .action(ctx -> {
                String result = ctx.value("String to change").asString();

                result = result.replace(ctx.value("String part to replace").asString(), ctx.value("Replacement").asString());

                ctx.setVariable("Result", new ScriptStringValue(result));
            })),

    REGEX_REPLACE_STRING(builder -> builder.name("Replace String using Regex")
            .description("Searches for part of a string\nusing a regex and replaces it.")
            .icon(Items.LEAD, true)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("String to change", ScriptActionArgumentType.STRING)
            .arg("Regex", ScriptActionArgumentType.STRING)
            .arg("Replacement", ScriptActionArgumentType.STRING)
            .category(ScriptActionCategory.STRINGS)
            .action(ctx -> {
                String result = ctx.value("String to change").asString();

                result = result.replaceAll(ctx.value("Regex").asString(), ctx.value("Replacement").asString());

                ctx.setVariable("Result", new ScriptStringValue(result));
            })),

    REMOVE_STRING(builder -> builder.name("Remove String")
            .description("Searches for part of a string and removes it.")
            .icon(Items.WRITABLE_BOOK)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("String to change", ScriptActionArgumentType.STRING)
            .arg("String to remove", ScriptActionArgumentType.STRING, b -> b.plural(true))
            .category(ScriptActionCategory.STRINGS)
            .action(ctx -> {
                String result = ctx.value("String to change").asString();

                List<ScriptValue> textsToRemove = ctx.pluralValue("String to remove");

                for (ScriptValue scriptValue : textsToRemove) {
                    result = result.replace(scriptValue.asString(), "");
                }

                ctx.setVariable("Result", new ScriptStringValue(result));
            })),

    STRING_UPPER(builder -> builder.name("Uppercase String")
            .description("Sets all characters in a string to uppercase")
            .icon(Items.IRON_INGOT)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("String", ScriptActionArgumentType.STRING)
            .category(ScriptActionCategory.STRINGS)
            .action(ctx -> {
                String result = ctx.value("String").asString();
                String upped = result.toUpperCase();
                ctx.setVariable("Result", new ScriptStringValue(upped));
            })),

    STRING_LOWER(builder -> builder.name("Lowercase String")
            .description("Sets all characters in a string to lowercase")
            .icon(Items.BRICK)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("String", ScriptActionArgumentType.STRING)
            .category(ScriptActionCategory.STRINGS)
            .action(ctx -> {
                String result = ctx.value("String").asString();
                String lowered = result.toLowerCase();
                ctx.setVariable("Result", new ScriptStringValue(lowered));
            })),

    REPEAT_STRING(builder -> builder.name("Repeat String")
            .description("Repeats a string the given number of times.")
            .icon(Items.REPEATING_COMMAND_BLOCK)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("String to repeat", ScriptActionArgumentType.STRING)
            .arg("Times to repeat", ScriptActionArgumentType.NUMBER)
            .category(ScriptActionCategory.STRINGS)
            .action(ctx -> {
                String input = ctx.value("String to repeat").asString();
                int times = (int) ctx.value("Times to repeat").asNumber();

                String result = input.repeat(Math.max(0, times));

                ctx.setVariable("Result", new ScriptStringValue(result));
            })),

    FORMAT_TIME(builder -> builder.name("Format Timestamp")
            .description("Turns a timestamp (ms) into human readable time.")
            .icon(Items.CLOCK)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Timestamp", ScriptActionArgumentType.NUMBER)
            .arg("Format", ScriptActionArgumentType.STRING)
            .category(ScriptActionCategory.STRINGS)
            .action(ctx -> {
                Date date = new Date((long) ctx.value("Timestamp").asNumber());
                SimpleDateFormat format = new SimpleDateFormat(ctx.value("Format").asString());

                ctx.setVariable("Result", new ScriptStringValue(format.format(date)));
            })),

    //////////////////
    /* DICTIONARIES */
    //////////////////

    CREATE_DICT(builder -> builder.name("Create Dictionary")
            .description("Creates a new dictionary.")
            .icon(Items.ENDER_CHEST)
            .category(ScriptActionCategory.DICTIONARIES)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Keys", ScriptActionArgumentType.LIST, b -> b.optional(true))
            .arg("Values", ScriptActionArgumentType.LIST, b -> b.optional(true))
            .action(ctx -> {

                HashMap<String, ScriptValue> dict = new HashMap<>();

                if (ctx.argMap().containsKey("Keys") && ctx.argMap().containsKey("Values")) {
                    List<ScriptValue> keys = ctx.value("Keys").asList();
                    List<ScriptValue> values = ctx.value("Values").asList();

                    // make sure we don't iterate past the end of a list
                    int lowerLength = Math.min(keys.size(), values.size());

                    for (int i = 0; i < lowerLength; i++) {
                        dict.put(keys.get(i).asString(), values.get(i));
                    }
                }

                ctx.setVariable("Result", new ScriptDictionaryValue(dict));
            })),

    PARSE_JSON(builder -> builder.name("Parse from JSON")
            .description("Creates a dict from JSON data.")
            .icon(Items.ANVIL)
            .category(ScriptActionCategory.DICTIONARIES)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("JSON", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                ScriptValue dict;

                try {
                    dict = ScriptValueJson.fromJson(JsonParser.parseString(ctx.value("JSON").toString()));
                } catch (JsonParseException e) {
                    dict = new ScriptUnknownValue();
                }


                ctx.setVariable("Result", dict);
            })),

    GET_DICT_VALUE(builder -> builder.name("Get Dictionary Value")
            .description("Gets a value from a dictionary.")
            .icon(Items.BOOK)
            .category(ScriptActionCategory.DICTIONARIES)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Dictionary", ScriptActionArgumentType.DICTIONARY)
            .arg("Key", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
                String key = ctx.value("Key").asString();
                if (dict.containsKey(key)) {
                    ctx.setVariable("Result", dict.get(key));
                } else {
                    ctx.setVariable("Result", new ScriptUnknownValue());
                }
            })),

    SET_DICT_VALUE(builder -> builder.name("Set Dictionary Value")
            .description("Sets a value in a dictionary.")
            .icon(Items.WRITABLE_BOOK)
            .category(ScriptActionCategory.DICTIONARIES)
            .arg("Dictionary", ScriptActionArgumentType.VARIABLE)
            .arg("Key", ScriptActionArgumentType.STRING)
            .arg("Value", ScriptActionArgumentType.ANY)
            .action(ctx -> {
                HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
                String key = ctx.value("Key").asString();
                dict.put(key, ctx.value("Value"));
                ctx.setVariable("Dictionary", new ScriptDictionaryValue(dict));
            })),

    GET_DICT_SIZE(builder -> builder.name("Get Dictionary Size")
            .description("Gets the size of a dictionary.")
            .icon(Items.BOOKSHELF)
            .category(ScriptActionCategory.DICTIONARIES)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Dictionary", ScriptActionArgumentType.DICTIONARY)
            .action(ctx -> {
                HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
                ctx.setVariable("Result", new ScriptNumberValue(dict.size()));
            })),

    GET_DICT_KEYS(builder -> builder.name("Get Dictionary Keys")
            .description("Gets a list of the keys in a dictionary.")
            .icon(Items.FURNACE_MINECART)
            .category(ScriptActionCategory.DICTIONARIES)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Dictionary", ScriptActionArgumentType.DICTIONARY)
            .action(ctx -> {
                HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
                ctx.setVariable("Result", new ScriptListValue(dict.keySet().stream().map(x -> (ScriptValue) new ScriptStringValue(x)).toList()));
            })
    ),

    REMOVE_DICT_ENTRY(builder -> builder.name("Remove Dictionary Entry")
            .description("Removes a key from a dictionary.")
            .icon(Items.TNT_MINECART)
            .category(ScriptActionCategory.DICTIONARIES)
            .arg("Dictionary", ScriptActionArgumentType.VARIABLE)
            .arg("Key", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
                String key = ctx.value("Key").asString();
                dict.remove(key);
                ctx.setVariable("Dictionary", new ScriptDictionaryValue(dict));
            })),

    ///////////
    /* MENUS */
    ///////////

    OPEN_MENU(builder -> builder.name("Open Menu")
            .description("Opens a custom empty menu.")
            .icon(Items.PAINTING)
            .category(ScriptActionCategory.MENUS)
            .arg("Width", ScriptActionArgumentType.NUMBER)
            .arg("Height", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                int width = (int) ctx.value("Width").asNumber();
                int height = (int) ctx.value("Height").asNumber();

                DFScript.MC.send(() -> {
                    DFScript.MC.setScreen(new ScriptMenu(width, height, ctx.task().context().script()));
                    DFScript.LOGGER.info("New menu: w=" + width + " h=" + height);
                    DFScript.LOGGER.info(DFScript.MC.currentScreen.getTitle().getString());
                });
            })),

    ADD_MENU_BUTTON(builder -> builder.name("Add Menu Button")
            .description("Adds a button to an open custom menu.")
            .icon(Items.CHISELED_STONE_BRICKS)
            .category(ScriptActionCategory.MENUS)
            .arg("X", ScriptActionArgumentType.NUMBER)
            .arg("Y", ScriptActionArgumentType.NUMBER)
            .arg("Width", ScriptActionArgumentType.NUMBER)
            .arg("Height", ScriptActionArgumentType.NUMBER)
            .arg("Text", ScriptActionArgumentType.TEXT)
            .arg("Identifier", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                int x = (int) ctx.value("X").asNumber();
                int y = (int) ctx.value("Y").asNumber();
                int width = (int) ctx.value("Width").asNumber();
                int height = (int) ctx.value("Height").asNumber();
                Text text = ctx.value("Text").formatAsText();
                String identifier = ctx.value("Identifier").asString();

                DFScript.MC.send(() -> {
                    if (DFScript.MC.currentScreen instanceof ScriptMenu menu) {
                        if (menu.ownedBy(ctx.task().context().script())) {
                            menu.widgets.add(new ScriptMenuButton(x, y, width, height, text, identifier, ctx.task().context().script()));
                        } else {
                            OverlayManager.getInstance().add("Unable to add button to menu! (Not owned by script)");
                        }
                    } else {
                        menu_CheckMenuIsNull("add button to menu");
                    }
                });
            })),

    ADD_MENU_ITEM(builder -> builder.name("Add Menu Item")
            .description("Adds an item to an open custom menu.")
            .icon(Items.ITEM_FRAME)
            .category(ScriptActionCategory.MENUS)
            .arg("X", ScriptActionArgumentType.NUMBER)
            .arg("Y", ScriptActionArgumentType.NUMBER)
            .arg("Item", ScriptActionArgumentType.DICTIONARY)
            .arg("Identifier", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                int x = (int) ctx.value("X").asNumber();
                int y = (int) ctx.value("Y").asNumber();
                ItemStack item = ScriptValueItem.itemFromValue(ctx.value("Item"));
                String identifier = ctx.value("Identifier").asString();

                DFScript.MC.send(() -> {
                    if (DFScript.MC.currentScreen instanceof ScriptMenu menu) {
                        if (menu.ownedBy(ctx.task().context().script())) {
                            menu.widgets.add(new ScriptMenuItem(x, y, item, identifier));
                        } else {
                            OverlayManager.getInstance().add("Unable to add item to menu! (Not owned by script)");
                        }
                    } else {
                        menu_CheckMenuIsNull("add item to menu");
                    }
                });
            })),

    ADD_MENU_TEXT(builder -> builder.name("Add Menu Text")
            .description("Adds text to an open custom menu.")
            .icon(Items.WRITTEN_BOOK)
            .category(ScriptActionCategory.MENUS)
            .arg("X", ScriptActionArgumentType.NUMBER)
            .arg("Y", ScriptActionArgumentType.NUMBER)
            .arg("Text", ScriptActionArgumentType.TEXT)
            .arg("Identifier", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                int x = (int) ctx.value("X").asNumber();
                int y = (int) ctx.value("Y").asNumber();
                String identifier = ctx.value("Identifier").asString();
                Text t = ctx.value("Text").formatAsText();

                DFScript.MC.send(() -> {
                    if (DFScript.MC.currentScreen instanceof ScriptMenu menu) {
                        if (menu.ownedBy(ctx.task().context().script())) {
                            menu.widgets.add(new ScriptMenuText(x, y, t, 0x333333, 1, false, false, identifier));
                        } else {
                            OverlayManager.getInstance().add("Unable to add text to menu! (Not owned by script)");
                        }
                    } else {
                        menu_CheckMenuIsNull("add text to menu");
                    }
                });
            })),

    ADD_MENU_TEXT_FIELD(builder -> builder.name("Add Menu Text Field")
            .description("Adds a text field to an open custom menu.")
            .icon(Items.WRITABLE_BOOK)
            .category(ScriptActionCategory.MENUS)
            .arg("X", ScriptActionArgumentType.NUMBER)
            .arg("Y", ScriptActionArgumentType.NUMBER)
            .arg("Width", ScriptActionArgumentType.NUMBER)
            .arg("Height", ScriptActionArgumentType.NUMBER)
            .arg("Identifier", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                int x = (int) ctx.value("X").asNumber();
                int y = (int) ctx.value("Y").asNumber();
                int width = (int) ctx.value("Width").asNumber();
                int height = (int) ctx.value("Height").asNumber();
                String identifier = ctx.value("Identifier").asString();

                DFScript.MC.send(() -> {
                    if (DFScript.MC.currentScreen instanceof ScriptMenu menu) {
                        if (menu.ownedBy(ctx.task().context().script())) {
                            menu.widgets.add(new ScriptMenuTextField("", x, y, width, height, true, identifier));
                        } else {
                            OverlayManager.getInstance().add("Unable to add text field to menu! (Not owned by script)");
                        }
                    } else {
                        menu_CheckMenuIsNull("add text field to menu");
                    }
                });
            })),

    REMOVE_MENU_ELEMENT(builder -> builder.name("Remove Menu Element")
            .description("Removes an element from an open custom menu.")
            .icon(Items.TNT_MINECART)
            .category(ScriptActionCategory.MENUS)
            .arg("Identifier", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                String identifier = ctx.value("Identifier").asString();

                DFScript.MC.send(() -> {
                    if (DFScript.MC.currentScreen instanceof ScriptMenu menu) {
                        if (menu.ownedBy(ctx.task().context().script())) {
                            menu.removeChild(identifier);
                        } else {
                            OverlayManager.getInstance().add("Unable to remove element from menu! (Not owned by script)");
                        }
                    } else {
                        menu_CheckMenuIsNull("remove element from menu");
                    }
                });
            })),

    GET_MENU_TEXT_FIELD_VALUE(builder -> builder.name("Get Menu Text Field Value")
            .description("Gets the string inside a text field in an open custom menu.")
            .icon(Items.BOOKSHELF)
            .category(ScriptActionCategory.MENUS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Identifier", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                String identifier = ctx.value("Identifier").asString();

                menu_Check(ctx, "get text field value", (ScriptMenuTextField field) -> {
                    ctx.setVariable(
                            "Result",
                            new ScriptStringValue(field.getText())
                    );
                }, identifier, ScriptMenuTextField.class);
            })),

    SET_MENU_TEXT_FIELD_VALUE(builder -> builder.name("Set Menu Text Field Value")
            .description("Sets the string inside a text field in an open custom menu.")
            .icon(Items.KNOWLEDGE_BOOK)
            .category(ScriptActionCategory.MENUS)
            .arg("Identifier", ScriptActionArgumentType.STRING)
            .arg("Value", ScriptActionArgumentType.STRING)
            .action(ctx -> {
                String identifier = ctx.value("Identifier").asString();

                menu_Check(ctx, "set text field value", (ScriptMenuTextField field) -> {
                    field.setText(ctx.value("Value").asString());
                }, identifier, ScriptMenuTextField.class);
            })),

    CLOSE_MENU(builder -> builder.name("Close Menu")
            .description("Closes the current menu")
            .category(ScriptActionCategory.MENUS)
            .icon(Items.BARRIER)
            .action(ctx -> DFScript.MC.send(() -> DFScript.MC.setScreen(null)))),

    /////////////
    /* CONTROL */
    /////////////

    CANCEL_EVENT(builder -> builder.name("Cancel Event")
            .description("Cancels the event.")
            .icon(Items.BARRIER)
            .category(ScriptActionCategory.CONTROL)
            .action(ctx -> {
                if (ctx.task().event() instanceof CancellableEvent ce) {
                    ce.setCancelled(true);
                }
            })),

    UNCANCEL_EVENT(builder -> builder.name("Uncancel Event")
            .description("Uncancels the event.")
            .icon(Items.STRUCTURE_VOID)
            .category(ScriptActionCategory.CONTROL)
            .action(ctx -> {
                if (ctx.task().event() instanceof CancellableEvent ce) {
                    ce.setCancelled(false);
                }
            })),

    WAIT(builder -> builder.name("Wait")
            .description("Waits for a given amount of time.")
            .icon(Items.CLOCK)
            .category(ScriptActionCategory.CONTROL)
            .arg("Ticks", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                for (int i = 0; i < ctx.task().stack().size(); i++) {
                    if (ctx.task().stack().peek(i).getParent() instanceof ScriptRepetition) {
                        ctx.task().stack().peek(i).setVariable("Lagslayer Count", 0);
                    }
                }

                ctx.task().stop();//Stop the current thread
                Scheduler.schedule((int) ctx.value("Ticks").asNumber(), () -> ctx.task().run());//Resume the task after the given amount of ticks
            })),

    STOP(builder -> builder.name("Halts")
            .description("Halts the current thread")
            .icon(Items.BARRIER)
            .category(ScriptActionCategory.CONTROL)
            .action(ctx -> {
                ctx.task().stop();
            })),

    SKIP_ITERATION(builder -> builder.name("Continue")
            .description("Skips the current iteration of the latest loop.\nContinues to the next.")
            .icon(Items.ENDER_PEARL)
            .category(ScriptActionCategory.CONTROL)
            .action(ctx -> {
                while (ctx.task().stack().size() > 0) {
                    if (ctx.task().stack().peek().getParent() instanceof ScriptRepetition) {
                        ctx.task().stack().peek().skip();
                        break;
                    }
                    ctx.task().stack().pop();
                }
            })),

    STOP_REPETITION(builder -> builder.name("Break")
            .description("Stops the latest loop.\nBreaks out of the current loop.")
            .icon(Items.PRISMARINE_SHARD)
            .category(ScriptActionCategory.CONTROL)
            .action(ctx -> {
                while (ctx.task().stack().size() > 0) {
                    if (ctx.task().stack().peek().getParent() instanceof ScriptRepetition) {
                        ctx.task().stack().pop();
                        break;
                    }
                    ctx.task().stack().pop();
                }
            })),

    ////////////////
    /* DEPRECATED */
    ////////////////

    TEXT_SUBTEXT_V1(builder -> builder.name("Get Subtext OLD")
            .description("Gets a piece of text within another text.")
            .icon(Items.KNOWLEDGE_BOOK)
            .category(ScriptActionCategory.TEXTS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Text", ScriptActionArgumentType.TEXT)
            .arg("First Index", ScriptActionArgumentType.NUMBER)
            .arg("Last Index", ScriptActionArgumentType.NUMBER)
            .deprecate(GET_SUBSTRING)
            .action(ctx -> {
                String text = ctx.value("Text").asString();
                int start = (int) ctx.value("First Index").asNumber() + 1;
                int end = (int) ctx.value("Last Index").asNumber();
                String result = text.substring(start, end);
                ctx.setVariable("Result", new ScriptTextValue(result));
            })),

    RANDOM_NUMBER(builder -> builder.name("Random Number")
            .description("Generates a random number between two other numbers.")
            .icon(Items.HOPPER)
            .category(ScriptActionCategory.NUMBERS)
            .deprecate(RANDOM_DOUBLE)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Min", ScriptActionArgumentType.NUMBER)
            .arg("Max", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                double min = ctx.value("Min").asNumber();
                double max = ctx.value("Max").asNumber();
                double result = Math.random() * (max - min) + min;
                ctx.setVariable(
                        "Result",
                        new ScriptNumberValue(result)
                );
            })),

    WRITE_FILE_OLD(builder -> builder.name("Write File OLD")
            .description("This action doesn't support all types of values.\nFiles written by this file can only be correctly read by the old Read File action.")
            .icon(Items.WRITABLE_BOOK)
            .category(ScriptActionCategory.MISC)
            .arg("Filename", ScriptActionArgumentType.TEXT)
            .arg("Content", ScriptActionArgumentType.ANY)
            .deprecate(WRITE_FILE)
            .action(ctx -> {
                String filename = ctx.value("Filename").asString();
                ScriptValue value = ctx.value("Content");

                if (filename.matches("^[a-zA-Z\\d_\\-. ]+$")) {
                    Path f = FileUtil.folder("Scripts").resolve(ctx.task().context().script().getFile().getName() + "-files").resolve(filename);
                    try {
                        f.toFile().getParentFile().mkdirs();
                        FileUtil.writeFile(f, ScriptValueJson.toJson(value).toString());
                    } catch (IOException e) {
//                    e.printStackTrace();
                        OverlayManager.getInstance().add("Internal error while writing file: " + filename);
                    }
                } else {
                    OverlayManager.getInstance().add("Illegal filename: " + filename);
                }
            })),


    READ_FILE_OLD(builder -> builder.name("Read File OLD")
            .description("This action doesn't support all types of values.\nThis action can only correctly read files written by the old Write File action.")
            .icon(Items.WRITTEN_BOOK)
            .category(ScriptActionCategory.MISC)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Filename", ScriptActionArgumentType.TEXT)
            .deprecate(READ_FILE)
            .action(ctx -> {
                String filename = ctx.value("Filename").asString();

                if (filename.matches("^[a-zA-Z\\d_\\-. ]+$")) {
                    Path f = FileUtil.folder("Scripts").resolve(ctx.task().context().script().getFile().getName() + "-files").resolve(filename);
                    if (Files.exists(f)) {
                        try {
                            String content = FileUtil.readFile(f);
                            JsonElement json = JsonParser.parseString(content);
                            ScriptValue value = ScriptValueJson.fromJson(json);
                            ctx.setVariable("Result", value);
                        } catch (IOException e) {
                            OverlayManager.getInstance().add("Internal error while reading file: " + filename);
                        }
                    }
                } else {
                    OverlayManager.getInstance().add("Illegal filename: " + filename);
                }
            })),

    DISPLAY_TITLE_OLD(builder -> builder.name("Display Title OLD")
            .description("Displays a title.")
            .deprecate(DISPLAY_TITLE)
            .icon(Items.WARPED_SIGN)
            .category(ScriptActionCategory.VISUALS)
            .arg("Title", ScriptActionArgumentType.TEXT)
            .arg("Subtitle", ScriptActionArgumentType.TEXT, b -> b.rightOptional(true).defaultValue(""))
            .arg("Fade In", ScriptActionArgumentType.NUMBER, b -> b.rightOptional(true).defaultValue(20))
            .arg("Stay", ScriptActionArgumentType.NUMBER, b -> b.rightOptional(true).defaultValue(60))
            .arg("Fade Out", ScriptActionArgumentType.NUMBER, b -> b.rightOptional(true).defaultValue(20))
            .action(ctx -> {
                String title = ctx.value("Title").asString();
                String subtitle = "";
                int fadeIn = 20;
                int stay = 60;
                int fadeOut = 20;

                if (ctx.argMap().containsKey("Subtitle")) {
                    subtitle = ctx.value("Subtitle").asString();
                }

                if (ctx.argMap().containsKey("Fade In")) {
                    fadeIn = (int) ctx.value("Fade In").asNumber();
                }

                if (ctx.argMap().containsKey("Stay")) {
                    stay = (int) ctx.value("Stay").asNumber();
                }

                if (ctx.argMap().containsKey("Fade Out")) {
                    fadeOut = (int) ctx.value("Fade Out").asNumber();
                }

                io.github.techstreet.dfscript.DFScript.MC.inGameHud.setTitle(ComponentUtil.fromString(ComponentUtil.andsToSectionSigns(title)));
                io.github.techstreet.dfscript.DFScript.MC.inGameHud.setSubtitle(ComponentUtil.fromString(ComponentUtil.andsToSectionSigns(subtitle)));
                io.github.techstreet.dfscript.DFScript.MC.inGameHud.setTitleTicks(fadeIn, stay, fadeOut);
            })),

    PLAY_SOUND_OLD(builder -> builder.name("Play Sound OLD")
            .description("Plays a sound.")
            .deprecate(PLAY_SOUND)
            .icon(Items.NAUTILUS_SHELL)
            .category(ScriptActionCategory.VISUALS)
            .arg("Sound", ScriptActionArgumentType.TEXT)
            .arg("Volume", ScriptActionArgumentType.NUMBER, b -> b.rightOptional(true).defaultValue(1))
            .arg("Pitch", ScriptActionArgumentType.NUMBER, b -> b.rightOptional(true).defaultValue(1))
            .action(ctx -> {
                String sound = ctx.value("Sound").asString();
                double volume = 1;
                double pitch = 1;

                if (ctx.argMap().containsKey("Volume")) {
                    volume = ctx.value("Volume").asNumber();
                }

                if (ctx.argMap().containsKey("Pitch")) {
                    pitch = ctx.value("Pitch").asNumber();
                }

                Identifier sndid = null;
                SoundManager sndManager = io.github.techstreet.dfscript.DFScript.MC.getSoundManager();

                try {
                    sndid = new Identifier(sound);
                } catch (Exception err) {
                    err.printStackTrace();
                    OverlayManager.getInstance().add("Incorrect identifier: " + sound);
                    return;
                }

                if (sndManager.getKeys().contains(sndid)) {
                    SoundEvent snd = SoundEvent.of(sndid);
                    sndManager.play(PositionedSoundInstance.master(snd, (float) pitch, (float) volume));
                } else {
                    OverlayManager.getInstance().add("Unknown sound: " + sound);

                    try {
                        String jname = StringUtil.fromSoundIDToRegistryID(sound);

                        List<String> similiar = new ArrayList<>();

                        int counter = 0;
                        for (Identifier id : sndManager.getKeys()) {
                            String sid = id.toString();
                            String name = StringUtil.fromSoundIDToRegistryID(sid);
                            if (name.contains(jname)) {
                                similiar.add(sid);
                                counter++;
                                if (counter > 5) {
                                    break;
                                }
                            }
                        }

                        if (similiar.size() > 0) {
                            OverlayManager.getInstance().add("Did you mean: \n" + String.join(", \n", similiar));
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }));

    private static <T extends ScriptWidget> void menu_Check(ScriptActionContext ctx, String verb, Consumer<T> cb, String identifier, Class<?> widgetClass) {
        DFScript.MC.send(() -> {
            if (DFScript.MC.currentScreen instanceof ScriptMenu menu) {
                if (menu.ownedBy(ctx.task().context().script())) {
                    ScriptWidget w = menu.getWidget(identifier);
                    if (widgetClass.isInstance(w)) {
                        T wC = (T) w;
                        cb.accept(wC);
                    } else {
                        OverlayManager.getInstance().add("Unable to " + verb + "! (Unknown widget type)");
                    }
                } else {
                    OverlayManager.getInstance().add("Unable to " + verb + "! (Not owned by script)");
                }
            } else {
                menu_CheckMenuIsNull(verb);
            }
        });
    }

    private static void menu_CheckMenuIsNull(String verb) {
        if (DFScript.MC.currentScreen != null) {
            OverlayManager.getInstance().add("Unable to " + verb + "! (Unknown menu type, title=" + DFScript.MC.currentScreen.getTitle().getString() + ")");
            DFScript.LOGGER.error("Unknown menu type: " + DFScript.MC.currentScreen.getTitle().getString());
        } else {
            OverlayManager.getInstance().add("Unable to " + verb + "! (No menu is present)");
            DFScript.LOGGER.error("No menu is present");
        }
    }

    private Consumer<ScriptActionContext> action = (ctx) -> {
    };

    private boolean glow = false;
    private Item icon = Items.STONE;
    private String name = "Unnamed Action";
    private boolean hasChildren = false;
    private ScriptActionCategory category = ScriptActionCategory.MISC;
    private List<String> description = new ArrayList();

    private ScriptActionType deprecated = null; //if deprecated == null, the action is not deprecated
    private final ScriptActionArgumentList arguments = new ScriptActionArgumentList();

    ScriptActionType(Consumer<ScriptActionType> builder) {
        description.add("No description provided.");
        builder.accept(this);
    }

    public ItemStack getIcon() {
        ItemStack item = new ItemStack(icon);

        item.setCustomName(Text.literal(name)
                .fillStyle(Style.EMPTY
                        .withColor(Formatting.WHITE)
                        .withItalic(false)));

        NbtList lore = new NbtList();

        if (isDeprecated()) {
            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal("This action is deprecated!")
                    .fillStyle(Style.EMPTY
                            .withColor(Formatting.RED)
                            .withItalic(false)))));
            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal("Use '" + deprecated.getName() + "'")
                    .fillStyle(Style.EMPTY
                            .withColor(Formatting.RED)
                            .withItalic(false)))));
        }

        for (String descriptionLine : description) {
            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal(descriptionLine)
                    .fillStyle(Style.EMPTY
                            .withColor(Formatting.GRAY)
                            .withItalic(false)))));
        }

        lore.add(NbtString.of(Text.Serializer.toJson(Text.literal(""))));

        for (ScriptActionArgument arg : arguments) {
            for (Text txt : arg.text()) {
                lore.add(NbtString.of(Text.Serializer.toJson(txt)));
            }
        }

        item.getSubNbt("display")
                .put("Lore", lore);

        if (glow) {
            item.addEnchantment(Enchantments.UNBREAKING, 1);
            item.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        }

        return item;
    }

    public String getName() {
        return name;
    }

    public boolean isDeprecated() {
        return deprecated != null;
    }

    public boolean hasChildren() {
        return hasChildren;
    }

    public ScriptActionCategory getCategory() {
        return category;
    }

    private ScriptActionType action(Consumer<ScriptActionContext> action) {
        this.action = action;
        return this;
    }

    private ScriptActionType icon(Item icon, boolean glow) {
        this.icon = icon;
        this.glow = glow;
        return this;
    }

    private ScriptActionType icon(Item icon) {
        icon(icon, false);
        return this;
    }

    private ScriptActionType name(String name) {
        this.name = name;
        return this;
    }

    private ScriptActionType hasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
        return this;
    }

    private ScriptActionType category(ScriptActionCategory category) {
        this.category = category;
        return this;
    }

    private ScriptActionType description(String description) {
        this.description.clear();
        this.description.addAll(Arrays.asList(description.split("\n", -1)));
        return this;
    }

    public ScriptActionType arg(String name, ScriptActionArgumentType type, Consumer<ScriptActionArgument> builder) {
        ScriptActionArgument arg = new ScriptActionArgument(name, type);
        builder.accept(arg);
        arguments.add(arg);
        return this;
    }

    public ScriptActionType arg(String name, ScriptActionArgumentType type) {
        return arg(name, type, (arg) -> {
        });
    }

    public ScriptActionType deprecate(ScriptActionType newScriptActionType) {
        deprecated = newScriptActionType;

        return this;
    }

    public void run(ScriptActionContext ctx) {
        try {
            arguments.getArgMap(ctx);
            action.accept(ctx);
        } catch (IllegalArgumentException e) {
            OverlayManager.getInstance().add("Invalid arguments for " + name + ".");
        }
    }
}
