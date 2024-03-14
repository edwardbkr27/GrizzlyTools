package me.couph.grizzlytools.Items;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.clip.placeholderapi.PlaceholderAPI;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.util.CouphUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ExplosivePickaxe extends GrizzlyPickaxe {

    private List<Block> currBlocks;
    public ExplosivePickaxe() {
        super("Explosive", ChatColor.BLUE);
        setAbility("Explosive");
        setRarity("MYTHICAL");
        setAbilityDescription("Cause an explosion in a 3x3 radius on every block broken.");
    }
    public GrizzlyPickaxe getPickaxe() {
        return this;
    }

    public void onBlockBreak(Player player, ItemStack item, Collection<ItemStack> drops, Block block) {
        try {
            performAbility(player, item, drops, block);
        } catch (Exception e) {
            CouphUtil.log("Explo desync", CouphUtil.LogLevel.ERROR);
        }
    }

    public void performAbility(Player player, ItemStack item1, Collection<ItemStack> drops, Block block) {
        if (block.getType() == Material.AIR) return;
        Random rand = new Random();
        int num = rand.nextInt(4);
        int dropamount = num+1;
        List<Block> blocksinarea = getBlocksIn3x3Area(block.getLocation());
        for (Block b : blocksinarea) {
            if (!(b == null)) {
                if (b.getType() != Material.BEDROCK) {
                    if (canBreakBlock(player, b)) {
                        Material oldmat = b.getType();
                        if (oldmat == Material.COAL_ORE) oldmat = Material.COAL;
                        if (oldmat == Material.QUARTZ_ORE) oldmat = Material.QUARTZ;
                        if (oldmat == Material.DIAMOND_ORE) oldmat = Material.DIAMOND;
                        if (oldmat == Material.EMERALD_ORE) oldmat = Material.EMERALD;
                        if (oldmat == Material.STONE) oldmat = Material.COBBLESTONE;
                        this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, oldmat, dropamount);
                        b.setType(Material.AIR);
                        if (PlaceholderAPI.setPlaceholders(player, "%AFF%").equalsIgnoreCase("true")) {
                            BlockBreakEvent blockEvent = new BlockBreakEvent(b, player);
                            Bukkit.getPluginManager().callEvent(blockEvent);
                        }
                    }
                }
            }
        }
    }

    public List<Block> getBlocksIn3x3Area(Location location) {
                List<Block> blocks = new ArrayList<>();
                World world = location.getWorld();
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();

                for (int xOffset = -1; xOffset <= 1; xOffset++) {
                    for (int yOffset = -1; yOffset <= 1; yOffset++) {
                        for (int zOffset = -1; zOffset <= 1; zOffset++) {
                            Block block = world.getBlockAt(x + xOffset, y + yOffset, z + zOffset);
                            if (block.getType() != Material.AIR) {
                                if (block.getType() != Material.BEDROCK) {
                                    blocks.add(block);
                                }
                            }
                        }
                    }
                }

                return blocks;
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