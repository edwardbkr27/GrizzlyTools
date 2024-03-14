package me.couph.grizzlytools.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PickaxeHandler {
    private GrizzlyTools plugin;

    public GrizzlyTools getPlugin() {
        return this.plugin;
    }
    private Map<Player, GrizzlyPickaxe> activePickaxeMap = Maps.newHashMap();

    public Map<Player, GrizzlyPickaxe> getActivePickaxeMap() {
        return this.activePickaxeMap;
    }
    private List<GrizzlyPickaxe> grizzlyPickaxes = Lists.newArrayList();
    private PickaxeStatusManager pickaxeStatusManager;

    public List<GrizzlyPickaxe> getGrizzlyPickaxes() {
        return this.grizzlyPickaxes;
    }

    public PickaxeStatusManager getPickaxeStatusManager() {
        return this.pickaxeStatusManager;
    }

    public PickaxeHandler(GrizzlyTools plugin) {
        this.plugin = plugin;
        registerGrizzlyPickaxes();
        this.pickaxeStatusManager = new PickaxeStatusManager(this);
    }

    public void registerGrizzlyPickaxes() {
        this.grizzlyPickaxes.forEach(HandlerList::unregisterAll);
        this.grizzlyPickaxes.clear();
        registerGrizzlyPickaxe((GrizzlyPickaxe)new RegularPickaxe());
        registerGrizzlyPickaxe((GrizzlyPickaxe)new LuckyPickaxe());
        registerGrizzlyPickaxe((GrizzlyPickaxe)new BountifulPickaxe());
        registerGrizzlyPickaxe((GrizzlyPickaxe)new ExplosivePickaxe());
        registerGrizzlyPickaxe((GrizzlyPickaxe)new MimicPickaxe());
        registerGrizzlyPickaxe((GrizzlyPickaxe)new PolarPickaxe());
        registerGrizzlyPickaxe((GrizzlyPickaxe)new PhantomPickaxe());
        registerGrizzlyPickaxe((GrizzlyPickaxe)new NuclearPickaxe());
        registerGrizzlyPickaxe((GrizzlyPickaxe)new DreamPickaxe());
        registerGrizzlyPickaxe((GrizzlyPickaxe)new BoosterPickaxe());
        this.activePickaxeMap.keySet().forEach(player -> {
            GrizzlyPickaxe grizzlyPickaxe = getByName(((GrizzlyPickaxe)this.activePickaxeMap.get(player)).getName());
            CouphUtil.sendMessage((LivingEntity)player, "&a&l(!)&7 Your " + grizzlyPickaxe.getColorBold() + grizzlyPickaxe.getName() + " Pickaxe&7 has been &a&nupdated&7.");
            this.activePickaxeMap.put(player, grizzlyPickaxe);
        });
    }

    public void recalculatePickaxeStatuses() {
        Maps.newHashMap(this.activePickaxeMap).forEach((player, pickaxe) -> {
            if (this.pickaxeStatusManager.isDisabled(pickaxe)) {
                pickaxe.onUnequip(player);
                setActivePickaxe(player, null);
                String message = this.plugin.getConfig().getString("messages.set-unequipped");
            }
        });
    }


    public void registerGrizzlyPickaxe(GrizzlyPickaxe grizzlyPickaxe) {
        this.grizzlyPickaxes.add(grizzlyPickaxe);
    }

    public GrizzlyPickaxe getByName(String name) {
        return this.grizzlyPickaxes.stream().filter(grizzlyPickaxe -> grizzlyPickaxe.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean isPickaxeEnabled(GrizzlyPickaxe grizzlyPickaxe) {
        return this.pickaxeStatusManager.isEnabled(grizzlyPickaxe);
    }

    public boolean isPickaxeDisabled(GrizzlyPickaxe grizzlyPickaxe) {
        return !isPickaxeEnabled(grizzlyPickaxe);
    }

    public GrizzlyPickaxe getByItem(ItemStack item) {
        return this.grizzlyPickaxes.stream().filter(grizzlyPickaxe -> grizzlyPickaxe.isGrizzlyPickaxe(item)).findFirst().orElse(null);
    }
    public GrizzlyPickaxe getByClass(Class<? extends GrizzlyPickaxe> clazz) {
        return this.grizzlyPickaxes.stream().filter(grizzlyPickaxe -> grizzlyPickaxe.getClass().equals(clazz)).findFirst().orElse(null);
    }
    public GrizzlyPickaxe getActivePickaxe(Player player) {
//        if (player.hasMetadata("apickaxetype")
//            return null;

        CouphUtil.sendMessage(player, String.valueOf(this.activePickaxeMap.size()));
        return this.activePickaxeMap.getOrDefault(player, null);
    }

    public boolean hasActivePickaxe(Player player) {
        return this.activePickaxeMap.containsKey(player);
    }

    public boolean isGrizzlyPickaxe(ItemStack item) {
        return (getByItem(item) != null);
    }

    public boolean isGrizzlyPickaxe(String name) {
        return (getByName(name) != null);
    }

    public void setActivePickaxe(Player player, GrizzlyPickaxe grizzlyPickaxe) {
        if (grizzlyPickaxe == null) {
            this.activePickaxeMap.remove(player);
        } else {
            this.activePickaxeMap.put(player, grizzlyPickaxe);
        }
    }

    public List<ItemStack> getPickaxesInInventory(Player player) {
        List<ItemStack> pickaxes = new ArrayList<>();
        for (ItemStack i : player.getInventory().getContents()) {
            if (getByItem(i) != null) {
                pickaxes.add(i);
            }
        }
        return pickaxes;
    }

    public void setPickaxeLevel(ItemStack item, Integer level) {
        GrizzlyPickaxe pickaxe = getByItem(item);
        pickaxe.setLevel(level);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        int lastIndex = lore.size() - 1;
        int secondLastIndex = lastIndex - 1;
        if (lastIndex >= 1) {
            lore.subList(secondLastIndex, lastIndex + 1).clear();
        }
        lore.add(pickaxe.getColor() + "Pickaxe EXP: " + ChatColor.GRAY + "0" + "/" + pickaxe.getLevelUpRequirement(level+1));
        lore.add(pickaxe.getColor() + "Pickaxe Level: " + ChatColor.GRAY + pickaxe.getLevel());
        meta.setLore(lore);
        String newDisplayName = pickaxe.getColorBold() + pickaxe.getName() + pickaxe.getColor() + pickaxe.getColorBold() + " Pickaxe " + ChatColor.GRAY + ChatColor.BOLD + "[" + pickaxe.getColor() + String.valueOf(level) + ChatColor.GRAY + ChatColor.BOLD + "]";
        meta.setDisplayName(newDisplayName);
        item.setItemMeta(meta);
    }

    public void getGui(Player player) {
        List<ItemStack> pickaxes = new ArrayList<>();
        for (GrizzlyPickaxe pick : getGrizzlyPickaxes()) {
            pickaxes.add(pick.createPickaxe(Material.DIAMOND_PICKAXE));
        }
        GuiUtil.openGui(player, pickaxes);
        player.sendMessage(CouphUtil.color("&a(!) You have opened the Enchantments GUI."));
    }
}

