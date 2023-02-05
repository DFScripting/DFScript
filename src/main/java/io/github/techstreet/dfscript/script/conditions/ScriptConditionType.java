package io.github.techstreet.dfscript.script.conditions;

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
import io.github.techstreet.dfscript.script.ScriptGroup;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.action.ScriptActionArgumentList;
import io.github.techstreet.dfscript.script.action.ScriptActionCategory;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.menu.*;
import io.github.techstreet.dfscript.script.util.ScriptValueItem;
import io.github.techstreet.dfscript.script.util.ScriptValueJson;
import io.github.techstreet.dfscript.script.values.*;
import io.github.techstreet.dfscript.util.*;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum ScriptConditionType {

    IF_EQUALS(builder -> builder.name("Equals")
        .description("Checks if one value is equal to another.")
        .icon(Items.IRON_INGOT)
        .category(ScriptActionCategory.VARIABLES)
        .arg("Value", ScriptActionArgumentType.ANY)
        .arg("Other", ScriptActionArgumentType.ANY)
        .action(ctx -> ctx.value("Value").valueEquals(ctx.value("Other")))),

    IF_NOT_EQUALS(builder -> builder.name("Not Equals")
        .description("Checks if one value is not equal to another.")
        .icon(Items.BARRIER)
        .category(ScriptActionCategory.VARIABLES)
        .arg("Value", ScriptActionArgumentType.ANY)
        .arg("Other", ScriptActionArgumentType.ANY)
        .deprecate(IF_EQUALS)
        .action(ctx -> !ctx.value("Value").valueEquals(ctx.value("Other")))),

    IF_GREATER(builder -> builder.name("Greater")
        .description("Checks if one number is greater than another.")
        .icon(Items.BRICK)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Value", ScriptActionArgumentType.NUMBER)
        .arg("Other", ScriptActionArgumentType.NUMBER)
        .action(ctx -> ctx.value("Value").asNumber() > ctx.value("Other").asNumber())),

    IF_GREATER_EQUALS(builder -> builder.name("Greater Equals")
        .description("Checks if one number is greater than or equal to another.")
        .icon(Items.BRICKS)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Value", ScriptActionArgumentType.NUMBER)
        .arg("Other", ScriptActionArgumentType.NUMBER)
        .action(ctx -> ctx.value("Value").asNumber() >= ctx.value("Other").asNumber())),

    IF_LESS(builder -> builder.name("Less")
        .description("Checks if one number is less than another.")
        .icon(Items.NETHER_BRICK)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Value", ScriptActionArgumentType.NUMBER)
        .arg("Other", ScriptActionArgumentType.NUMBER)
        .action(ctx -> ctx.value("Value").asNumber() < ctx.value("Other").asNumber())),

    IF_LESS_EQUALS(builder -> builder.name("If Less Equals")
        .description("Checks if one number is less than or equal to another.")
        .icon(Items.NETHER_BRICKS)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Value", ScriptActionArgumentType.NUMBER)
        .arg("Other", ScriptActionArgumentType.NUMBER)
        .action(ctx -> ctx.value("Value").asNumber() <= ctx.value("Other").asNumber())),

    IF_WITHIN_RANGE(builder -> builder.name("Number Within Range")
            .description("Checks if a number is between\n2 different numbers (inclusive).")
            .icon(Items.CHEST)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Value", ScriptActionArgumentType.NUMBER)
            .arg("Minimum", ScriptActionArgumentType.NUMBER)
            .arg("Maximum", ScriptActionArgumentType.NUMBER)
            .action(ctx -> {
                double value = ctx.value("Value").asNumber();

                if (value >= ctx.value("Minimum").asNumber()) {
                    if (value <= ctx.value("Maximum").asNumber()) {
                        return true;
                    }
                }
                return false;
            })),

    IF_NOT_WITHIN_RANGE(builder -> builder.name("Number Not Within Range")
            .description("Checks if a number isn't between\n2 different numbers (inclusive).")
            .icon(Items.TRAPPED_CHEST)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Value", ScriptActionArgumentType.NUMBER)
            .arg("Minimum", ScriptActionArgumentType.NUMBER)
            .arg("Maximum", ScriptActionArgumentType.NUMBER)
            .deprecate(IF_WITHIN_RANGE)
            .action(ctx -> {
                double value = ctx.value("Value").asNumber();

                if (value >= ctx.value("Minimum").asNumber()) {
                    if (value <= ctx.value("Maximum").asNumber()) {
                        return false;
                    }
                }

                return true;
            })),

    IF_LIST_CONTAINS(builder -> builder.name("List Contains")
        .description("Checks if a list contains a value.")
        .icon(Items.BOOKSHELF)
        .category(ScriptActionCategory.LISTS)
        .arg("List", ScriptActionArgumentType.LIST)
        .arg("Value", ScriptActionArgumentType.ANY)
        .action(ctx -> {
            List<ScriptValue> list = ctx.value("List").asList();
            return list.stream().anyMatch(value -> value.valueEquals(ctx.value("Value")));
        })),

    IF_TEXT_CONTAINS(builder -> builder.name("Text Contains")
        .description("Checks if a text contains a value.")
        .icon(Items.NAME_TAG)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Subtext", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String subtext = ctx.value("Subtext").asText();
            return text.contains(subtext);
        })),

    IF_MATCHES_REGEX(builder -> builder.name("Matches Regex")
        .description("Checks if a text matches a regex.")
        .icon(Items.ANVIL)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Regex", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String regex = ctx.value("Regex").asText();
            return text.matches(regex);
        })),

    IF_STARTS_WITH(builder -> builder.name("Starts With")
        .description("Checks if a text starts with an other.")
        .icon(Items.FEATHER)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Subtext", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String subtext = ctx.value("Subtext").asText();
            return text.startsWith(subtext);
        })),

    IF_LIST_DOESNT_CONTAIN(builder -> builder.name("List Doesnt Contain")
        .description("Checks if a list doesnt contain a value.")
        .icon(Items.BOOKSHELF)
        .category(ScriptActionCategory.LISTS)
        .arg("List", ScriptActionArgumentType.LIST)
        .arg("Value", ScriptActionArgumentType.ANY)
        .deprecate(IF_LIST_CONTAINS)
        .action(ctx -> {
            List<ScriptValue> list = ctx.value("List").asList();
            return list.stream().noneMatch(value -> value.valueEquals(ctx.value("Value")));
        })),

    IF_TEXT_DOESNT_CONTAIN(builder -> builder.name("Text Doesnt Contain")
        .description("Checks if a text doesnt contain a value.")
        .icon(Items.NAME_TAG)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Subtext", ScriptActionArgumentType.TEXT)
        .deprecate(IF_TEXT_CONTAINS)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String subtext = ctx.value("Subtext").asText();
            return !text.contains(subtext);
        })),

    IF_DOESNT_START_WITH(builder -> builder.name("Doesnt Start With")
        .description("Checks if a text doesnt start with an other.")
        .icon(Items.FEATHER)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Subtext", ScriptActionArgumentType.TEXT)
        .deprecate(IF_STARTS_WITH)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String subtext = ctx.value("Subtext").asText();
            return !text.startsWith(subtext);
        })),

    IF_DOESNT_MATCH_REGEX(builder -> builder.name("Doesnt Match Regex")
        .description("Checks if a text doesnt match a regex.")
        .icon(Items.ANVIL)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Regex", ScriptActionArgumentType.TEXT)
        .deprecate(IF_MATCHES_REGEX)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String regex = ctx.value("Regex").asText();
            return !text.matches(regex);
        })),

    IF_DICT_KEY_EXISTS(builder -> builder.name("Dictionary Key Exists")
        .description("Checks if a key exists in a dictionary.")
        .icon(Items.NAME_TAG)
        .category(ScriptActionCategory.DICTIONARIES)
        .arg("Dictionary", ScriptActionArgumentType.DICTIONARY)
        .arg("Key", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
            String key = ctx.value("Key").asText();
            return dict.containsKey(key);
        })),

    IF_DICT_KEY_DOESNT_EXIST(builder -> builder.name("Dictionary Key Doesnt Exist")
        .description("Checks if a key doesnt exist in a dictionary.")
        .icon(Items.NAME_TAG)
        .category(ScriptActionCategory.DICTIONARIES)
        .arg("Dictionary", ScriptActionArgumentType.DICTIONARY)
        .arg("Key", ScriptActionArgumentType.TEXT)
        .deprecate(IF_DICT_KEY_EXISTS)
        .action(ctx -> {
            HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
            String key = ctx.value("Key").asText();
            return !dict.containsKey(key);
        })),

    IF_GUI_OPEN(builder -> builder.name("GUI Open")
        .description("Executes if a gui is open.")
        .icon(Items.BOOK)
        .category(ScriptActionCategory.MISC)
        .action(ctx -> {
            return DFScript.MC.currentScreen != null;
        })),

    IF_GUI_CLOSED(builder -> builder.name("GUI Not Open")
        .description("Executes if no gui is open.")
        .icon(Items.BOOK)
        .deprecate(IF_GUI_OPEN)
        .category(ScriptActionCategory.MISC)
        .action(ctx -> {
            return DFScript.MC.currentScreen == null;
        })),

    IF_FILE_EXISTS(builder -> builder.name("File Exists")
        .description("Executes if the specified file exists.")
        .icon(Items.BOOK)
        .category(ScriptActionCategory.MISC)
        .arg("Filename", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String filename = ctx.value("Filename").asText();
            if (filename.matches("^[a-zA-Z\\d_\\-\\. ]+$")) {
                Path f = FileUtil.folder("Scripts").resolve(ctx.task().context().script().getFile().getName()+"-files").resolve(filename);
                if (Files.exists(f)) {
                    return true;
                }
            } else {
                ChatUtil.error("Illegal filename: " + filename);
            }
            return false;
        })),

    IF_FILE_DOESNT_EXIST(builder -> builder.name("File Doesnt Exist")
        .description("Executes if the specified file doesnt exist.")
        .icon(Items.BOOK)
        .category(ScriptActionCategory.MISC)
        .arg("Filename", ScriptActionArgumentType.TEXT)
        .deprecate(IF_FILE_EXISTS)
        .action(ctx -> {
            String filename = ctx.value("Filename").asText();
            if (filename.matches("^[a-zA-Z\\d_\\-\\. ]+$")) {
                Path f = FileUtil.folder("Scripts").resolve(ctx.task().context().script().getFile().getName()+"-files").resolve(filename);
                if (!Files.exists(f)) {
                    return true;
                }
            } else {
                ChatUtil.error("Illegal filename: " + filename);
            }
            return false;
        })),

    TRUE(builder -> builder.name("True")
            .description("Always executes.\nLiterally the only reason for this is so the\nlegacy deserializer code doesn't have to discard ELSEs\nthat aren't tied to a CONDITION...")
            .icon(Items.LIME_WOOL)
            .category(null)
            .action(ctx -> true));

    private Function<ScriptActionContext, Boolean> action = (ctx) -> false;

    private boolean glow = false;
    private Item icon = Items.STONE;
    private String name = "Unnamed Condition";
    private boolean hasChildren = false;
    private ScriptActionCategory category = ScriptActionCategory.MISC;
    private List<String> description = new ArrayList();
    private ScriptGroup group = ScriptGroup.ACTION;

    private ScriptConditionType deprecated = null; //if deprecated == null, the action is not deprecated
    private final ScriptActionArgumentList arguments = new ScriptActionArgumentList();
    ScriptConditionType(Consumer<ScriptConditionType> builder) {
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

        if(isDeprecated())
        {
            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal("This action is deprecated!")
                    .fillStyle(Style.EMPTY
                            .withColor(Formatting.RED)
                            .withItalic(false)))));
            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal("Use '" + deprecated.getName() + "'")
                    .fillStyle(Style.EMPTY
                            .withColor(Formatting.RED)
                            .withItalic(false)))));
        }

        for (String descriptionLine: description) {
            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal(descriptionLine)
                .fillStyle(Style.EMPTY
                      .withColor(Formatting.GRAY)
                      .withItalic(false)))));
        }

        lore.add(NbtString.of(Text.Serializer.toJson(Text.literal(""))));

        for (ScriptActionArgument arg : arguments) {
            lore.add(NbtString.of(Text.Serializer.toJson(arg.text())));
        }

        item.getSubNbt("display")
            .put("Lore", lore);

        if(glow)
        {
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

    private ScriptConditionType action(Function<ScriptActionContext, Boolean> action) {
        this.action = action;
        return this;
    }

    private ScriptConditionType icon(Item icon, boolean glow) {
        this.icon = icon;
        this.glow = glow;
        return this;
    }

    private ScriptConditionType icon(Item icon) {
        icon(icon, false);
        return this;
    }

    private ScriptConditionType name(String name) {
        this.name = name;
        return this;
    }

    private ScriptConditionType hasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
        return this;
    }

    private ScriptConditionType category(ScriptActionCategory category) {
        this.category = category;
        return this;
    }

    private ScriptConditionType description(String description) {
        this.description.clear();
        this.description.addAll(Arrays.asList(description.split("\n", -1)));
        return this;
    }

    public ScriptConditionType arg(String name, ScriptActionArgumentType type, Consumer<ScriptActionArgument> builder) {
        ScriptActionArgument arg = new ScriptActionArgument(name, type);
        builder.accept(arg);
        arguments.add(arg);
        return this;
    }

    public ScriptConditionType arg(String name, ScriptActionArgumentType type) {
        return arg(name, type, (arg) -> {
        });
    }

    public ScriptConditionType deprecate(ScriptConditionType newScriptConditionType) {
        deprecated = newScriptConditionType;

        return this;
    }

    public boolean run(ScriptActionContext ctx) {
        try
        {
            arguments.getArgMap(ctx);
            return action.apply(ctx);
        }
        catch(IllegalArgumentException e)
        {
            ChatUtil.error("Invalid arguments for " + name + ".");
            return false;
        }
    }
}
