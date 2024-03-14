package me.couph.grizzlytools.Items;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.Random;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.util.CouphUtil;
import me.couph.grizzlytools.util.PickaxeUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class NuclearPickaxe extends GrizzlyPickaxe {
    public NuclearPickaxe() {
        super("Nuclear", ChatColor.RED);
        setAbility("Nuclear");
        setRarity("VERY RARE");
        setAbilityDescription("Passively drop tnt around you as you mine.");
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
        int proc = rand.nextInt(50);
        if (proc == 1) {
            TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
            tnt.setFuseTicks(60);
            tnt.setGravity(true);
            tnt.setIsIncendiary(false);
            Material mat = block.getType();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (regionIsMine(tnt.getLocation())) {
                        Random rand = new Random();
                        int amount = rand.nextInt(2500);
                        GrizzlyTools.getInstance().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, mat, amount);
                    }
                }
            }.runTaskLater(GrizzlyTools.getInstance(), 59);
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

    public boolean regionIsMine(Location locat) {
        locat.setY(locat.getY()-1);
        ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(locat);

        for (ProtectedRegion region : regions) {
            return region.getId().contains("mine");
        }
        return false;
    }
}
