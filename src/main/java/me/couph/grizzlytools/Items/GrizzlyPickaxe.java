package me.couph.grizzlytools.Items;


import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.util.CouphUtil;
import me.couph.grizzlytools.util.PickaxeUtil;
import me.couph.grizzlytools.util.PlaceHoldersUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.couph.grizzlytools.util.CouphUtil.LogLevel.INFO;

// level, attribute,
public abstract class GrizzlyPickaxe implements Listener {

    private String name;
    private String displayName;
    private ChatColor color;
    private String pickaxeIdentifier;

    private String ability;

    private HashMap<Integer, Integer> levelUpRequirements;

    private Integer level;
    private Integer EXP;
    private Boolean enabled;

    private Integer blocksBroken;

    private String abilityDescription;

    private String rarity;

    public void setName(String name) {
        this.name = name;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public void setBlocksBroken(Integer blocks) {
        this.blocksBroken = blocks;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setLevel(Integer level){
        this.level = level;
    }

    public void setLevelUpRequirements(HashMap<Integer, Integer> requirements) {
        this.levelUpRequirements = requirements;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setAbilityDescription(String abilityDescription) {
        this.abilityDescription = abilityDescription;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public void setEXP(Integer EXP) {
        this.EXP = EXP;
    }

    public void setPickaxeIdentifier(String identifier) {
        this.pickaxeIdentifier = identifier;
    }

    public String getName() {
        return this.name;
    }

    public Integer getEXP() {
        return this.EXP;
    }

    public Integer getBlocksBroken() {
        return this.blocksBroken;
    }

    public String getAbilityDescription() {
        return this.abilityDescription;
    }

    public String getRarity() {
        return this.rarity;
    }

    public String getPickaxeIdentifier() {
        return this.pickaxeIdentifier;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public Integer getLevel() {
        return this.level;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public Integer getLevelUpRequirement(Integer level) {
        return this.levelUpRequirements.get(level);
    }

    public HashMap<Integer, Integer> getLevelUpRequirements(HashMap<Integer, Integer> requirements) {
        return this.levelUpRequirements;
    }

    public String getAbility() {
        return this.ability;
    }

    public GrizzlyPickaxe(String name, ChatColor color) {
        this.name = name;
        this.color = color;
        this.enabled = Boolean.TRUE;
        this.pickaxeIdentifier = PlaceHoldersUtil.translatePlaceholders(getPlugin().getConfig().getString("pickaxe-identifier"), this);
        getPlugin().getServer().getPluginManager().registerEvents(this, (Plugin)getPlugin());
    }

    public ItemStack createPickaxe(Material material) {
        setEXP(0);
        setLevel(0);
        setBlocksBroken(0);
        setLevelUpRequirements(getRequirements());
        String itemName, name = (this.displayName == null) ? this.name : this.displayName;
        itemName = getColorBold() + name + getColor() + getColorBold() + " Pickaxe " + ChatColor.GRAY + ChatColor.BOLD + "[" + getColor() + this.level.toString() + ChatColor.GRAY + ChatColor.BOLD + "]";
        List<String> pickaxeLore = PlaceHoldersUtil.translatePlaceholders(CouphUtil.color(getPlugin().getConfig().getStringList("lore")), this);
        if (pickaxeLore.contains("%LIST_BONUS%")) {
            int index = pickaxeLore.indexOf("%LIST_BONUS%");
            pickaxeLore.set(index++, getColorBold() + getName().toUpperCase() + " PICKAXE ABILITY");
            pickaxeLore.add(index++, getColorBold() + "* " + getColor() + CouphUtil.color(getAbilityDescription()));
            if (getName().equalsIgnoreCase("Dream")) {
                pickaxeLore.add(index++, getColorBold() + "* " + getColor() + CouphUtil.color("3x Enchant Proc Rate"));
            }
            if (getName().equalsIgnoreCase("Booster")) {
                pickaxeLore.add(index++, getColorBold() + "* " + getColor() + CouphUtil.color("3x Passive XP Boost"));
            }
        }
        pickaxeLore.add("");
        pickaxeLore.add(CouphUtil.color(getColorBold() + "PICKAXE RARITY: " + this.getRarity()));
        pickaxeLore.add("");
        pickaxeLore.add(getColor() + "Blocks Broken: " + ChatColor.GRAY + this.blocksBroken);
        pickaxeLore.add(getColor() + "Pickaxe Level: " + ChatColor.GRAY + this.level);
        pickaxeLore.add(getColor() + "Pickaxe EXP: " + ChatColor.GRAY + this.EXP + "/1000");
        ItemStack item = CouphUtil.createItem(material, itemName, pickaxeLore);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(CouphUtil.color(pickaxeLore));
        item.setItemMeta(meta);
        item.addUnsafeEnchantments(PickaxeUtil.getEnchantments(this));
        return item;
    }

    public boolean isGrizzlyPickaxe(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta() || !item.getItemMeta().hasLore())
            return false;
        return item.getItemMeta().getLore().contains(CouphUtil.color(getPickaxeIdentifier()));
    }

    public GrizzlyTools getPlugin() {
        return GrizzlyTools.getInstance();
    }

    public FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }
    public void onEquip(Player pickaxeHolder) {}

    public void onUnequip(Player pickaxeHolder) {}

    public void onBlockBreak(Player player, ItemStack item, Collection<ItemStack> drops, Block block) {

    }
    public void reInitialise(Player player, GrizzlyPickaxe pickaxe) {
        setName(pickaxe.getName());
        setLevel(pickaxe.getLevel());
        setEXP(pickaxe.getEXP());
        setLevelUpRequirements(getRequirements());
        setAbility(pickaxe.getAbility());
    }

    public HashMap<Integer, Integer> getRequirements() {
        HashMap<Integer, Integer> HashMap = new HashMap<>();
        HashMap.put(1,1000);
        HashMap.put(2,2000);
        HashMap.put(3,4000);
        HashMap.put(4,7500);
        HashMap.put(5,10000);
        HashMap.put(6,10000);
        HashMap.put(7,10000);
        HashMap.put(8,10000);

        return HashMap;
    }

    public ItemMeta incrementEXP(Player player, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta = removeTinkererID(meta);
        List<String> lore = meta.getLore();
        int currEXP = 0;
        int currLevel = 0;
        int blocksBroken = 0;
        for (String loreLine : lore) {
            if (loreLine.contains("Blocks Broken:")) {
                loreLine = CouphUtil.colorAndStrip(loreLine);
                int colonIndex = loreLine.indexOf(":");
                if (colonIndex != -1) {
                    blocksBroken = Integer.parseInt(loreLine.substring(colonIndex + 1).trim());
                }
            }
            if (loreLine.contains("Pickaxe EXP:")) {
                loreLine = CouphUtil.colorAndStrip(loreLine);
                int slashIndex = loreLine.indexOf("/");
                if (slashIndex != -1) {
                    currEXP = Integer.parseInt(loreLine.substring(loreLine.indexOf(":") + 2, slashIndex).trim());
                }
            }
            if (loreLine.contains("Pickaxe Level:")) {
                loreLine = CouphUtil.colorAndStrip(loreLine);
                int colonIndex = loreLine.indexOf(":");
                if (colonIndex != -1) {
                    currLevel = Integer.parseInt(loreLine.substring(colonIndex + 1).trim());
                }
            }
        }
        currEXP++;
        if (!(currLevel >= 8)) {
            if (currEXP == getLevelUpRequirement(currLevel + 1)) {
                currEXP = 0;
                currLevel++;
                player.sendTitle(CouphUtil.color("&aPickaxe level up!"), CouphUtil.color("&2Level &2" + currLevel), 10, 50, 10);
                player.sendMessage("");
                player.sendMessage(CouphUtil.color("&d&l(!) &b&lYour pickaxe has leveled up to level " + currLevel + "!"));
                player.sendMessage("");
                CouphUtil.playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
            }
        } else {
            if (currEXP == 10000) {
                currEXP = 0;
                currLevel++;
                player.sendTitle(CouphUtil.color("&aPickaxe level up!"), CouphUtil.color("&2Level &2" + currLevel), 10, 50, 10);
                player.sendMessage("");
                player.sendMessage(CouphUtil.color("&d&l(!) &b&lYour pickaxe has leveled up to level " + currLevel + "!"));
                player.sendMessage("");
                CouphUtil.playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
            }
        }
        setEXP(currEXP);
        setLevel(currLevel);
        setBlocksBroken(blocksBroken + 1);
        int lastIndex = lore.size() - 1;
        int secondLastIndex = lastIndex - 1;
        int thirdLastIndex = lastIndex - 2;

        if (lastIndex >= 1) {
            lore.subList(thirdLastIndex, lastIndex + 1).clear();
        }
        if (!(currLevel >= 8)) {
            lore.add(getColor() + "Blocks Broken: " + ChatColor.GRAY + getBlocksBroken());
            lore.add(getColor() + "Pickaxe EXP: " + ChatColor.GRAY + getEXP() + "/" + getLevelUpRequirement(currLevel + 1));
            lore.add(getColor() + "Pickaxe Level: " + ChatColor.GRAY + getLevel());
            meta.setLore(lore);
            String newDisplayName = getColorBold() + this.getName() + getColor() + getColorBold() + " Pickaxe " + ChatColor.GRAY + ChatColor.BOLD + "[" + getColor() + String.valueOf(currLevel) + ChatColor.GRAY + ChatColor.BOLD + "]";
            meta.setDisplayName(newDisplayName);
            setXPBar(getEXP(), getLevelUpRequirement(currLevel + 1), player, getLevel());
        } else {
            lore.add(getColor() + "Blocks Broken: " + ChatColor.GRAY + getBlocksBroken());
            lore.add(getColor() + "Pickaxe EXP: " + ChatColor.GRAY + getEXP() + "/" + 10000);
            lore.add(getColor() + "Pickaxe Level: " + ChatColor.GRAY + getLevel());
            meta.setLore(lore);
            String newDisplayName = getColorBold() + this.getName() + getColor() + getColorBold() + " Pickaxe " + ChatColor.GRAY + ChatColor.BOLD + "[" + getColor() + String.valueOf(currLevel) + ChatColor.GRAY + ChatColor.BOLD + "]";
            meta.setDisplayName(newDisplayName);
            setXPBar(getEXP(), 10000, player, getLevel());
        }
        return meta;
    }

    public ItemMeta removeTinkererID(ItemMeta meta2) {
        List<String> lore2 = meta2.getLore();
        List<String> loreCopy = lore2;
        for (String loreLine2 : lore2) {
            if (CouphUtil.colorAndStrip(loreLine2).contains("Tinkerer ID")) {
                loreCopy.remove(loreLine2);
            }
        }
        meta2.setLore(loreCopy);
        return meta2;
    }

    public void setXPBar(Integer exp, Integer levelUp, Player player, Integer pickaxeLevel) {
        float percent = ((float)exp / (float)levelUp);
        player.setExp(percent);
        if (player.getLevel() != pickaxeLevel) player.setLevel(pickaxeLevel);
    }

    public String getColorBold() {
        return getColor().toString() + ChatColor.BOLD;
    }
}
