package me.couph.grizzlytools.Items;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Random;

public class PhantomPickaxe extends GrizzlyPickaxe {
    public PhantomPickaxe() {
        super("Phantom", ChatColor.GRAY);
        setAbility("Phantom");
        setRarity("RARE");
        setAbilityDescription("Break the same block multiple times.");
    }
    public GrizzlyPickaxe getPickaxe() {
        return this;
    }

    public void onBlockBreak(Player player, ItemStack item, Collection<ItemStack> drops, Block block) {
        if (!(canBreakBlock(player, block))) return;
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

    public boolean canBreakBlock(Player player, Block block) {
        Plugin plugin = WorldGuardPlugin.inst();
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(block.getLocation());

        for (ProtectedRegion region : regions) {
            Flag<?> blockBreakFlag = DefaultFlag.BLOCK_BREAK;
            Object flagValue = region.getFlag(blockBreakFlag);

            if (regions.testState(localPlayer, DefaultFlag.BLOCK_BREAK)) {
                return true;
            }
        }

        return false;
    }
}
