package me.couph.grizzlytools.Items;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import org.bukkit.*;
import java.util.ArrayList;
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
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class MimicPickaxe extends GrizzlyPickaxe {
    public MimicPickaxe() {
        super("Mimic", ChatColor.GREEN);
        setAbility("Mimic");
        setRarity("RARE");
        setAbilityDescription("Break the blocks around you as you mine.");
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
        Block extrablock = getRandomBlockIn5x5Radius(block.getLocation());
        for (ItemStack i : drops) {
            if (!(item1.containsEnchantment(Enchantment.SILK_TOUCH))) {
                this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, i.getType(), dropamount);
                //player.getInventory().addItem(new ItemStack(i.getType(), dropamount));
            }
            if (item1.containsEnchantment(Enchantment.SILK_TOUCH)) {
                Material old = i.getType();
                if (old == Material.COAL_ORE) old = Material.COAL;
                if (old == Material.QUARTZ_ORE) old = Material.QUARTZ;
                if (old == Material.DIAMOND_ORE) old = Material.DIAMOND;
                if (old == Material.EMERALD_ORE) old = Material.EMERALD;
                if (old == Material.STONE) old = Material.COBBLESTONE;
                //player.getInventory().addItem(new ItemStack(old, dropamount));
                this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, old, dropamount);
            }
        }
        if (!(item1.containsEnchantment(Enchantment.SILK_TOUCH))) {
            if (extrablock.getType() != Material.BEDROCK) {
                if (canBreakBlock(player, extrablock)) {
                    Material oldmat = extrablock.getType();
                    if (oldmat == Material.COAL_ORE) oldmat = Material.COAL;
                    if (oldmat == Material.QUARTZ_ORE) oldmat = Material.QUARTZ;
                    if (oldmat == Material.DIAMOND_ORE) oldmat = Material.DIAMOND;
                    if (oldmat == Material.EMERALD_ORE) oldmat = Material.EMERALD;
                    if (oldmat == Material.STONE) oldmat = Material.COBBLESTONE;
                    //player.getInventory().addItem(new ItemStack(oldmat, dropamount));
                    this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, oldmat, dropamount);
                    extrablock.setType(Material.AIR);
                }
            }
        }
        if (item1.containsEnchantment(Enchantment.SILK_TOUCH)) {
            if ((extrablock.getType() != Material.BEDROCK)) {
                if (canBreakBlock(player, extrablock)) {
                    //player.getInventory().addItem(new ItemStack(extrablock.getType(), dropamount));
                    this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, extrablock.getType(), dropamount);
                    extrablock.setType(Material.AIR);
                }
           }
        }
    }

    public Block getRandomBlockIn5x5Radius(Location location) {
        World world = location.getWorld();
        int centerX = location.getBlockX();
        int centerY = location.getBlockY();
        int centerZ = location.getBlockZ();
        List<Block> blocks = new ArrayList<>();

        for (int x = centerX - 2; x <= centerX + 2; x++) {
           // for (int y = centerY - 2; y <= centerY + 2; y++) {
                for (int z = centerZ - 2; z <= centerZ + 2; z++) {
                    Block block = world.getBlockAt(x, centerY, z);
                    blocks.add(block);
                }
            //}
        }

        Random random = new Random();
        Block randomBlock = blocks.get(random.nextInt(blocks.size()));
        for (int x=0; x<9; x++) {
            if (randomBlock.getType() == Material.AIR) {
                randomBlock = blocks.get(random.nextInt(blocks.size()));
            } else {
                break;
            }
        }

        return randomBlock;
    }

    public boolean canBreakBlock(Player player, Block block) {
        if (player.isOp()) return true;
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

