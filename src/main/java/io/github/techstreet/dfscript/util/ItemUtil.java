package io.github.techstreet.dfscript.util;

import io.github.techstreet.dfscript.DFScript;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;

public class ItemUtil {


    public static void giveCreativeItem(ItemStack item, boolean preferHand) {
        MinecraftClient mc = DFScript.MC;
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
                    if (index < 9) {
                        mc.interactionManager.clickCreativeStack(item, index + 36);
                    }
                    mainInventory.set(index, item);
                    return;
                }
            }
        }
    }
}
