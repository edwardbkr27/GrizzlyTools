package me.couph.grizzlytools.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

import java.util.*;

// Coal Ore ->  Coal -> Coal Block -> Quartz Ore -> Quartz -> Quart Block -> Lapis -> Lapis Ore -> Lapis Block -> Redstone -> Redstone Ore -> Redstone Block -> Iron Ore -> Iron -> Iron Block -> Gold Ore -> Gold -> Gold Block -> Diamond Ore -> Diamond -> Diamond Block -> Emerald Ore -> Emerald -> Emerald Block
// Base on the actual item dropped, remembering silk touch / future smeltering ability?

public class BountifulPickaxe extends GrizzlyPickaxe {

    private Map<Material, Integer> worth;
    public BountifulPickaxe() {
        super("Bountiful", ChatColor.LIGHT_PURPLE);
        setAbility("Bountiful");
        setRarity("RARE");
        setAbilityDescription("Replace the blocks you break with the most valuable block around it.");
        this.worth = new HashMap<>();
        worth.put(Material.COAL_ORE, 1);
        worth.put(Material.COAL, 2);
        worth.put(Material.COAL_BLOCK, 3);
        worth.put(Material.IRON_ORE, 12);
        worth.put(Material.IRON_INGOT, 13);
        worth.put(Material.IRON_BLOCK, 14);
        worth.put(Material.GOLD_ORE, 15);
        worth.put(Material.GOLD_INGOT, 16);
        worth.put(Material.GOLD_BLOCK, 17);
        worth.put(Material.DIAMOND_ORE, 18);
        worth.put(Material.DIAMOND, 19);
        worth.put(Material.DIAMOND_BLOCK, 20);
        worth.put(Material.EMERALD_ORE, 21);
        worth.put(Material.EMERALD, 22);
        worth.put(Material.EMERALD_BLOCK, 23);
        worth.put(Material.OBSIDIAN, 24);
    }

    public List<ItemStack> getBlockDrops(Location location) {
        List<ItemStack> drops = new ArrayList<>();
        World world = location.getWorld();

        if (world != null) {
            int centerX = location.getBlockX();
            int centerY = location.getBlockY();
            int centerZ = location.getBlockZ();

            // Iterate over the blocks in a 3x3 radius
            for (int x = centerX - 1; x <= centerX + 1; x++) {
                for (int y = centerY - 1; y <= centerY + 1; y++) {
                    for (int z = centerZ - 1; z <= centerZ + 1; z++) {
                        Location blockLocation = new Location(world, x, y, z);
                        Block block = world.getBlockAt(blockLocation);
                        Material blockType = block.getType();

                        // Add the dropped item to the list
                        if (blockType != Material.AIR && blockType != Material.BEDROCK) {
                            drops.add(new ItemStack(blockType));
                        }
                    }
                }
            }
        }

        return drops;
    }

    public GrizzlyPickaxe getPickaxe() {
        return this;
    }

    public static Material findHighestPriorityMaterial(Map<Material, Integer> worth, List<Material> materialList) {
        Material highestPriorityMaterial = null;
        int highestPriority = Integer.MIN_VALUE;

        for (Material material : materialList) {
            if (worth.containsKey(material)) {
                int priority = worth.get(material);
                if (priority > highestPriority) {
                    highestPriority = priority;
                    highestPriorityMaterial = material;
                }
            }
        }
        return highestPriorityMaterial;
    }

    public void onBlockBreak(Player player, ItemStack item, Collection<ItemStack> drops, Block block) {
        performAbility(player, item, drops, block);
    }

    public void performAbility(Player player, ItemStack item1, Collection<ItemStack> originaldrops, Block block) {
        int dropamount = 1;
        ItemStack originalitem = originaldrops.stream().findFirst().orElse(new ItemStack(Material.AIR));
        if (item1.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
            Random rand = new Random();
            int num = rand.nextInt(100);
            if (num > 50) {
                dropamount = 2;
            } else {
                dropamount = 3;
            }
        }
        World world = block.getWorld();
        Location location = block.getLocation();
        List<ItemStack> drops = getBlockDrops(location);
        List<Material> drops2 = new ArrayList<>();
        for (ItemStack item : drops) {
            if (!(item1.containsEnchantment(Enchantment.SILK_TOUCH))) {
                Material oldmat = item.getType();
                if (oldmat == Material.COAL_ORE) oldmat = Material.COAL;
                if (oldmat == Material.QUARTZ_ORE) oldmat = Material.QUARTZ;
                if (oldmat == Material.DIAMOND_ORE) oldmat = Material.DIAMOND;
                if (oldmat == Material.EMERALD_ORE) oldmat = Material.EMERALD;
                drops2.add(oldmat);
            } else {
                drops2.add(item.getType());
            }
        }
        Material newmaterial = findHighestPriorityMaterial(this.worth, drops2);
        if (newmaterial == null) {
            newmaterial = originalitem.getType();
        }
        ItemStack itemtoadd = new ItemStack(newmaterial, dropamount);
        this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, itemtoadd.getType(), dropamount);
        //player.getInventory().addItem(itemtoadd);
        // get blocks in 3x3
        // get what that block would drop
        // add to list
        // give list to findhighestprioritymaterial
        // drop that material instead and take into account fortune
    }

}
