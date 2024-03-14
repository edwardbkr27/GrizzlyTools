package me.couph.grizzlytools.Listeners;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sun.org.apache.xpath.internal.operations.Bool;
import me.clip.placeholderapi.PlaceholderAPI;
import me.couph.grizzlytools.Gameplay.LuckyBlock;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import me.couph.grizzlytools.util.CouphUtil;
import me.couph.grizzlytools.util.PickaxeHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.*;

public class GrizzlyPickaxeListener implements Listener {
    private final GrizzlyTools plugin;

    private Map<Player, List<ItemStack>> storedPickaxes;

    public GrizzlyPickaxeListener(GrizzlyTools plugin) {
        this.plugin = plugin;
        this.storedPickaxes = new HashMap<>();
    }

    @EventHandler
    // manage pick equip
    public void onPickaxeEquip(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        int slot = event.getNewSlot();
        ItemStack item = event.getPlayer().getInventory().getItem(slot);
        if (item != null && item.getType() == Material.DIAMOND_PICKAXE) {
            short maxDurability = Material.DIAMOND_PICKAXE.getMaxDurability();
            item.setDurability((short) 0);
        }
        GrizzlyPickaxe grizzlyPickaxe = this.plugin.getGrizzlyPickaxeHandler().getByItem(item);
        if (grizzlyPickaxe == null) {
            this.plugin.getGrizzlyPickaxeHandler().setActivePickaxe(player, grizzlyPickaxe);
            return;
        }
        if (this.plugin.getGrizzlyPickaxeHandler().hasActivePickaxe(player)) {
            this.plugin.getGrizzlyPickaxeHandler().setActivePickaxe(player, null);
            this.plugin.getGrizzlyPickaxeHandler().setActivePickaxe(player, grizzlyPickaxe);
            grizzlyPickaxe.onEquip(player);
            return;
        }
        if (this.plugin.getGrizzlyPickaxeHandler().isPickaxeDisabled(grizzlyPickaxe)) {
            return;
        }
        this.plugin.getGrizzlyPickaxeHandler().setActivePickaxe(player, grizzlyPickaxe);
        grizzlyPickaxe.onEquip(player);
    }

    @EventHandler
    // always keep pickaxe
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        PickaxeHandler handler = this.plugin.getGrizzlyPickaxeHandler();
        if (event.getEntity().getWorld() != Bukkit.getWorld("PvE")) {
            this.storedPickaxes.put(event.getEntity(), handler.getPickaxesInInventory(event.getEntity()));

            List<ItemStack> itemsToRemove = new ArrayList<>();
            for (ItemStack item : event.getDrops()) {
                if (item.getType() == Material.DIAMOND_PICKAXE) {
                    itemsToRemove.add(item);
                }
            }

            event.getDrops().removeAll(itemsToRemove);
        }
    }

    @EventHandler
    // always keep pickaxe
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        if (this.storedPickaxes.containsKey(event.getPlayer())) {
            for (ItemStack item : this.storedPickaxes.get(event.getPlayer())) {
                event.getPlayer().getInventory().addItem(item);
            }
            this.storedPickaxes.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (this.plugin.getGrizzlyPickaxeHandler().getByItem(event.getItem()) != null) {
                //cmd
            }
        }
    }


    @EventHandler
    public void playerLogOn(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (GrizzlyPickaxe grizzlyPickaxe : this.plugin.getGrizzlyPickaxeHandler().getGrizzlyPickaxes()) {
            for (ItemStack i : player.getInventory().getContents()) {
                if (grizzlyPickaxe.isGrizzlyPickaxe(i)) {
                    this.plugin.getGrizzlyPickaxeHandler().setActivePickaxe(player, grizzlyPickaxe);
                    CouphUtil.sendMessage(player, "&a&l(!)&7 Your " + grizzlyPickaxe.getColorBold() + grizzlyPickaxe.getName() + " Pickaxe&7 has been &a&nenabled&7.");
                    this.plugin.getGrizzlyPickaxeHandler().getByItem(i).reInitialise(player, this.plugin.getGrizzlyPickaxeHandler().getByItem(i));
                }
            }
        }
        // if player has no pickaxe or backpack, add
    }

    public Boolean hasAffinity(Player player) {
        return Boolean.valueOf(PlaceholderAPI.setPlaceholders(player, "%AFF%"));
    }

    public Boolean hasSwiftnessPet(Player player) {
        return Boolean.valueOf(PlaceholderAPI.setPlaceholders(player, "%SWI%"));
    }

    public Integer getSwiftnessLevel(Player player) {
        return Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%SWILevel%"));
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    //manage block break
    public void handleBlockBreakInMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (!(regionIsMine(player, block))) return;
        event.setDropItems(false);
        event.setExpToDrop(0);
        ItemStack item = player.getInventory().getItemInMainHand();
        Collection<ItemStack> droppedItems = event.getBlock().getDrops();

        restoreDura(player);

        Random rand = new Random();

        GrizzlyPickaxe grizzlyPickaxe = this.plugin.getGrizzlyPickaxeHandler().getByItem(item);
        if (grizzlyPickaxe == null) {
            return;
        }

        if (block.getType() != Material.AIR) {
            ItemMeta meta = grizzlyPickaxe.incrementEXP(player, item);
            item.setItemMeta(meta);
            if (grizzlyPickaxe.getName().equalsIgnoreCase("Explosive")) {
                for (int x = 0; x<rand.nextInt(3); x++) {
                    ItemMeta meta2 = grizzlyPickaxe.incrementEXP(player, item);
                    item.setItemMeta(meta2);
                }
                if (hasAffinity(player)) {
                    for (int x = 0; x<rand.nextInt(3); x++) {
                        ItemMeta meta2 = grizzlyPickaxe.incrementEXP(player, item);
                        item.setItemMeta(meta2);
                    }
                }
            }
            if (grizzlyPickaxe.getName().equalsIgnoreCase("Booster")) {
                for (int x = 0; x<2; x++) {
                    ItemMeta meta2 = grizzlyPickaxe.incrementEXP(player, item);
                    item.setItemMeta(meta2);
                }
            }
        }

        grizzlyPickaxe.onBlockBreak(player, item, droppedItems, block);

        if (grizzlyPickaxe.getName().equalsIgnoreCase("Phantom")) {
            if (!(rand.nextInt(10) == 1)) event.setCancelled(true);
        }


        if (block.getType() != Material.AIR) doLuckyBlock(player, grizzlyPickaxe.getName());

        if (hasSwiftnessPet(player)) addSwiftness(player);
    }

    public void addSwiftness(Player player) {
        int level = getSwiftnessLevel(player)+2;
        player.removePotionEffect(PotionEffectType.SPEED);
        PotionEffect swiftnessEffect = new PotionEffect(PotionEffectType.SPEED, 10 * 20, level - 1);
        player.addPotionEffect(swiftnessEffect);
    }

    public void restoreDura(Player player) {
        if (player.getInventory().contains(Material.DIAMOND_PICKAXE)) {
            for (ItemStack i : player.getInventory().getContents()) {
                if (!(i == null || i.getType() == null)) {
                    if (i.getType() == Material.DIAMOND_PICKAXE) {
                        short currDura = i.getDurability();
                        short maxDura = i.getType().getMaxDurability();
                        short remDura = (short) (maxDura - currDura);
                        if (remDura <= 40) {
                            player.getInventory().getItemInMainHand().setDurability((short) 0);
                        }
                    }
                }
            }
        }
    }

    public void doLuckyBlock(Player player, String pickName) {
        Random rand = new Random();
        int bound = 50000;
        String LBB = PlaceholderAPI.setPlaceholders(player, "%LBB%");
        if (LBB.equalsIgnoreCase("true")) {
            String level = PlaceholderAPI.setPlaceholders(player, "%LBBLevel%");
            if (level.equalsIgnoreCase("0")) {
                bound = 46000;
            }
            if (level.equalsIgnoreCase("1")) {
                bound = 42000;
            }
            if (level.equalsIgnoreCase("2")) {
                bound = 38000;
            }
            if (level.equalsIgnoreCase("3")) {
                bound = 34000;
            }
            if (level.equalsIgnoreCase("4")) {
                bound = 30000;
            }
            if (level.equalsIgnoreCase("5")) {
                bound = 26000;
            }
        }
        if (pickName.equalsIgnoreCase("Explosive")) bound = bound*18;
        if (pickName.equalsIgnoreCase("Dream")) bound = bound/3;

        if (bound < 15000) {
            bound = 15000;
        }

        // 2x lucky block days
        if (PlaceholderAPI.setPlaceholders(player, "%luckyblockevent%").equals("true")) {
            bound = bound / 2;
        }

        if (rand.nextInt(bound) == 1) {
            int tier = rand.nextInt(100)+1;
            if (tier <= 50) { // 50% Chance
                LuckyBlock luckyBlock = new LuckyBlock(this.plugin, "Common");
                luckyBlock.spawnBlock(player);
                return;
            }
            if (tier <= 75) { // 25% Chance
                LuckyBlock luckyBlock = new LuckyBlock(this.plugin, "Rare");
                luckyBlock.spawnBlock(player);
                return;
            }
            if (tier <= 90) { // 15%
                LuckyBlock luckyBlock = new LuckyBlock(this.plugin, "Legendary");
                luckyBlock.spawnBlock(player);
                return;
            } // 10%
            LuckyBlock luckyBlock = new LuckyBlock(this.plugin, "Mythical");
            luckyBlock.spawnBlock(player);
            return;
        }
    }

    public double getPercentageChance(int num, int bound) {
        double result = ((double) num / bound) * 100.0;

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(result));
    }

    public boolean regionIsMine(Player player, Block block) {
        if (player.isOp()) return true;
        ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(block.getLocation());

        for (ProtectedRegion region : regions) {
            return region.getId().contains("mine");
        }
        return false;
    }
}
