package me.couph.grizzlytools.Items;

import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.Random;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.util.CouphUtil;
import me.couph.grizzlytools.util.PickaxeUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class LuckyPickaxe extends GrizzlyPickaxe {
    public LuckyPickaxe() {
        super("Lucky", ChatColor.GOLD);
        setAbility("Lucky");
        setRarity("VERY RARE");
        setAbilityDescription("Chance to multiply the drops given from a broken block.");
    }
    public GrizzlyPickaxe getPickaxe() {
        return this;
    }

    public void onBlockBreak(Player player, ItemStack item, Collection<ItemStack> drops, Block block) {
        performAbility(player, item, drops, block);
    }

    public void performAbility(Player player, ItemStack item1, Collection<ItemStack> drops, Block block) {
        Random rand = new Random();
        int randint = rand.nextInt(100);
        int dropamount = 1;
        if (item1.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
            int num = rand.nextInt(100);
            if (num > 50) {
                dropamount = 2;
            } else {
                dropamount = 3;
            }
        }
        for (ItemStack item : drops) {
            int amount = item.getAmount();
            if (randint < 10) {
                amount = amount * 4;
                amount = amount + dropamount;
                item.setAmount(amount);
                //player.getInventory().addItem(item);
                this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, item.getType(), amount);
                return;
            }
            if (randint < 25) {
                amount = amount * 3;
                amount = amount + dropamount;
                item.setAmount(amount);
                this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, item.getType(), amount);
                //player.getInventory().addItem(item);
                return;
            }
            if (randint < 50) {
                amount = amount * 2;
                amount = amount + dropamount;
                item.setAmount(amount);
                this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, item.getType(), amount);
                //player.getInventory().addItem(item);
                return;
            }
            amount = amount + dropamount;
            item.setAmount(amount);
            this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, item.getType(), amount);
            //player.getInventory().addItem(item);
            return;
        }
    }
}
