package me.couph.grizzlytools.Items;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Random;

public class DreamPickaxe extends GrizzlyPickaxe {

    public DreamPickaxe() {
        super("Dream", ChatColor.YELLOW);
        setAbility("Dream");
        setRarity("MYTHICAL");
        setAbilityDescription("3x Chance of Lucky Block");
    }
    public GrizzlyPickaxe getPickaxe() {
        return this;
    }

    public void onBlockBreak(Player player, ItemStack item, Collection<ItemStack> drops, Block block) {
        performAbility(player, item, drops, block);
    }

    public void performAbility(Player player, ItemStack item1, Collection<ItemStack> drops, Block block) {
        Random rand = new Random();
        int dropamount = 1;
        if (item1.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
            int num = rand.nextInt(100);
            if (num > 50) {
                dropamount = 2;
            }
        }
        for (ItemStack item : drops) {
            item.setAmount(dropamount);
            this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, item.getType(), dropamount);
            return;
        }
    }
}
