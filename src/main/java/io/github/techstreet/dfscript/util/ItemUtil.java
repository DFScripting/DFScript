package io.github.techstreet.dfscript.util;

import java.util.ArrayList;
import java.util.List;

import io.github.techstreet.dfscript.DFScript;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public class ItemUtil {

    public static void giveItem(ItemStack item) {
        MinecraftClient mc = DFScript.MC;
        DefaultedList<ItemStack> mainInventory = mc.player.getInventory().main;

        for (int index = 0; index < mainInventory.size(); index++) {
            ItemStack i = mainInventory.get(index);
            ItemStack compareItem = i.copy();
            compareItem.setCount(item.getCount());
            if (item == compareItem) {
                while (i.getCount() < i.getMaxCount() && item.getCount() > 0) {
                    i.setCount(i.getCount() + 1);
                    item.setCount(item.getCount() - 1);
                }
            } else {
                if (i.getItem() == Items.AIR) {
                    if (index < 9)
                        MinecraftClient.getInstance().interactionManager.clickCreativeStack(item, index + 36);
                    mainInventory.set(index, item);
                    return;
                }
            }
        }
    }

    public static boolean handEmpty() {
        return MinecraftClient.getInstance().player.getMainHandStack().isEmpty();
    }

    public static void setHandItem(ItemStack item) {
        MinecraftClient mc = io.github.techstreet.dfscript.DFScript.MC;
        mc.interactionManager.clickCreativeStack(item, mc.player.getInventory().selectedSlot + 36);
    }

    public static NbtList toListTag(List<ItemStack> stacks) {
        NbtList listTag = new NbtList();
        for (ItemStack stack : stacks) {
            listTag.add(stack.writeNbt(new NbtCompound()));
        }

        return listTag;
    }
    
    public static List<ItemStack> fromListTag(NbtList listTag) {
        List<ItemStack> stacks = new ArrayList<>();
        for (NbtElement tag : listTag) {
            stacks.add(ItemStack.fromNbt((NbtCompound) tag));
        }
        return stacks;
    }

	public static void giveCreativeItem(ItemStack item, boolean preferHand) {
        MinecraftClient mc = io.github.techstreet.dfscript.DFScript.MC;
        DefaultedList<ItemStack> mainInventory = mc.player.getInventory().main;

        if (preferHand) {
            if (mc.player.getMainHandStack().isEmpty()) {
                mc.interactionManager.clickCreativeStack(item, mc.player.getInventory().selectedSlot + 36);
                return;
            }
        }

        for (int index = 0; index < mainInventory.size(); index++) {
            ItemStack i = mainInventory.get(index);
            ItemStack compareItem = i.copy();
            compareItem.setCount(item.getCount());
            if (item == compareItem) {
                while (i.getCount() < i.getMaxCount() && item.getCount() > 0) {
                    i.setCount(i.getCount() + 1);
                    item.setCount(item.getCount() - 1);
                }
            } else {
                if (i.getItem() == Items.AIR) {
                    if (index < 9)
                        mc.interactionManager.clickCreativeStack(item, index + 36);
                    mainInventory.set(index, item);
                    return;
                	}
            	}
        	}
    	}

	public static List<ItemStack> fromItemContainer(ItemStack container) {
        NbtList nbt = container.getOrCreateNbt().getCompound("BlockEntityTag").getList("Items", 10);
        return fromListTag(nbt);
    }

    public static void setLore(ItemStack itemStack, Text[] lores){
        NbtList loreTag = new NbtList();
        for(Text lore : lores) {
            if(lore == null){
                itemStack.getSubNbt("display").put("Lore", loreTag);
                return;
            }
            loreTag.add(NbtString.of("{\"extra\":[{\"bold\":" + lore.getStyle().isBold() + ",\"italic\":" + lore.getStyle().isItalic() + ",\"underlined\":" + lore.getStyle().isUnderlined() + ",\"strikethrough\":" + lore.getStyle().isStrikethrough() + ",\"obfuscated\":" + lore.getStyle().isObfuscated() + ",\"color\":\"" + lore.getStyle().getColor() + "\",\"text\":\"" + lore.getString() + "\"}],\"text\":\"\"}"));
        }
        itemStack.getSubNbt("display").put("Lore", loreTag);
    }

    public static ItemStack setLore(ItemStack itemStack, String[] lores){
        NbtList loreTag = new NbtList();
        for(String lore : lores) {
            if(lore == null){
                itemStack.getSubNbt("display").put("Lore", loreTag);
                return itemStack;
            }
            loreTag.add(NbtString.of(lore));
        }
        itemStack.getSubNbt("display").put("Lore", loreTag);
        return itemStack;
    }

    public static ItemStack addLore(ItemStack itemStack, String[] lores){
        NbtList loreTag = new NbtList();
        if(itemStack.getOrCreateSubNbt("display").contains("Lore")){
            loreTag = itemStack.getSubNbt("display").getList("Lore", 8);
        }
        for(String lore : lores) {
            if(lore == null){
                break;
            }
            loreTag.add(NbtString.of(lore));
        }
        itemStack.getSubNbt("display").put("Lore", loreTag);
        return itemStack;
    }

    public static ItemStack fromID(String id) {
        return new ItemStack(Registry.ITEM.get(new Identifier(id.toLowerCase())));
    }
}
