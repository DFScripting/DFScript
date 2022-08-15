package io.github.techstreet.dfscript.util.template;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TemplateUtil {

    public static final int VERSION = 1;

    public static void applyRawTemplateNBT(ItemStack stack, String name, String author, String codeData) {
        applyRawTemplateNBT(stack, Text.literal(name), author, codeData, VERSION);
    }

    public static void applyRawTemplateNBT(ItemStack stack, Text name, String author, String codeData, int version) {
        NbtCompound publicBukkitNBT = new NbtCompound();
        NbtCompound itemNBT = new NbtCompound();
        NbtCompound codeNBT = new NbtCompound();

        codeNBT.putString("name", name.toString());
        codeNBT.putString("author", author);
        codeNBT.putString("code", codeData);
        codeNBT.putInt("version", version);

        // Apply the template data to the item.
        publicBukkitNBT.putString("hypercube:codetemplatedata", codeNBT.toString());

        // Assign the bukkit container to the item. (Contains the template data)
        itemNBT.put("PublicBukkitValues", publicBukkitNBT);
        stack.setNbt(itemNBT);
        // stack.setCustomName(name);
    }


    public static void compressTemplateNBT(ItemStack stack, String name, String author, String template) {
        try {
            byte[] b64 = CompressionUtil.toBase64(CompressionUtil.toGZIP(template.getBytes(StandardCharsets.UTF_8)));
            String exported = new String(b64);
            applyRawTemplateNBT(stack, name, author, exported);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static JsonObject fromItemStack(ItemStack stack) {
        NbtCompound tag = stack.getNbt();
        NbtCompound publicBukkitNBT = tag.getCompound("PublicBukkitValues");
        String template = publicBukkitNBT.getString("hypercube:codetemplatedata");
        return JsonParser.parseString(template).getAsJsonObject();
    }

    public static boolean isTemplate(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        NbtCompound tag = stack.getNbt();
        if (tag == null) {
            return false;
        }

        NbtCompound publicBukkitNBT = tag.getCompound("PublicBukkitValues");
        if (publicBukkitNBT == null) {
            return false;
        }

        return publicBukkitNBT.getString("hypercube:codetemplatedata").length() > 0;
    }
}