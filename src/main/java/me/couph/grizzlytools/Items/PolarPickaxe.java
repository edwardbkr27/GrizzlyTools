package me.couph.grizzlytools.Items;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.clip.placeholderapi.PlaceholderAPI;
import me.couph.grizzlytools.util.GuiUtil;
import org.bukkit.*;

import java.util.Collection;
import java.util.Random;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.util.CouphUtil;
import me.couph.grizzlytools.util.PickaxeUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PolarPickaxe extends GrizzlyPickaxe {
    public PolarPickaxe() {
        super("Polar", ChatColor.WHITE);
        setAbility("Polar");
        setRarity("MYTHICAL");
        setAbilityDescription("Leave a trail of snow that thaws through the blocks you walk on.");
    }
    public GrizzlyPickaxe getPickaxe() {
        return this;
    }

    public void onBlockBreak(Player player, ItemStack item, Collection<ItemStack> drops, Block block) {
        performAbility(player, item, drops, block);
    }

    public void performAbility(Player player, ItemStack item1, Collection<ItemStack> drops, Block block) {
        return;
    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!(this.getPlugin().getGrizzlyPickaxeHandler().isGrizzlyPickaxe(player.getInventory().getItemInMainHand()))) return;
        GrizzlyPickaxe grizzlyPickaxe = this.getPlugin().getGrizzlyPickaxeHandler().getByItem(player.getInventory().getItemInMainHand());
        if (grizzlyPickaxe.getName().equalsIgnoreCase("Polar")) {
            Location l = event.getPlayer().getLocation();
            l.setY(l.getBlockY() - 1);
            if (l.getWorld().getBlockAt(l).getType() == Material.AIR) {
                Location newLoc = l;
                for (int x=0; x<5; x++) {
                    newLoc.setY(newLoc.getBlockY()-1);
                    if (l.getWorld().getBlockAt(newLoc).getType() != Material.AIR) {
                        l = newLoc;
                        break;
                    }
                }
            }
            if (l.getWorld().getBlockAt(l).getType() == Material.AIR || l.getWorld().getBlockAt(l).getType() == Material.BEDROCK || l.getWorld().getBlockAt(l).getType() == Material.SNOW_BLOCK) return;
            if (!(canBreakBlock(player, l.getWorld().getBlockAt(l)))) return;
            Material mat = l.getWorld().getBlockAt(l).getType();
            l.getWorld().getBlockAt(l).setType(Material.SNOW_BLOCK);
            Location finalL = l;
            new BukkitRunnable() {
                @Override
                public void run() {
                    finalL.getWorld().getBlockAt(finalL).setType(Material.AIR);
                    if (PlaceholderAPI.setPlaceholders(player, "%AFF%").equalsIgnoreCase("true")) {
                        BlockBreakEvent blockEvent = new BlockBreakEvent(finalL.getBlock(), player);
                        Bukkit.getPluginManager().callEvent(blockEvent);
                    }
                }
            }.runTaskLater(this.getPlugin(), 50L);
            Random rand = new Random();
            int num = rand.nextInt(100) + 1;
            if (mat != Material.SNOW_BLOCK && mat != Material.AIR) {
                this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, mat, num);
            }
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
