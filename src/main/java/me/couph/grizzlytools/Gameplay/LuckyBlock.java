package me.couph.grizzlytools.Gameplay;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.couph.grizzlybackpacks.util.CouphUtil;
import me.couph.grizzlytools.GrizzlyTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class LuckyBlock {

    private GrizzlyTools plugin;
    public String rarity;

    public LuckyBlock(GrizzlyTools plugin, String rarity) {
        this.plugin = plugin;
        this.rarity = rarity;
    }

    public void spawnBlock(Player player) {
        if (rarity.equalsIgnoreCase("Common")) player.sendTitle(CouphUtil.color("&6You broke a &aCommon &6Lucky Block!"), CouphUtil.color("&eYou were given a lucky block lootbox!"));
        if (rarity.equalsIgnoreCase("Rare")) player.sendTitle(CouphUtil.color("&6You broke a &9Rare &6Lucky Block!"), CouphUtil.color("&eYou were given a lucky block lootbox!"));
        if (rarity.equalsIgnoreCase("Legendary")) player.sendTitle(CouphUtil.color("&6You broke a &6Legendary &6Lucky Block!"), CouphUtil.color("&eYou were given a lucky block lootbox!"));
        if (rarity.equalsIgnoreCase("Mythical")) player.sendTitle(CouphUtil.color("&6You broke a &cMythical &6Lucky Block!"), CouphUtil.color("&eYou were given a lucky block lootbox!"));

        if (rarity.equalsIgnoreCase("Common")) Bukkit.broadcastMessage(CouphUtil.color("&6&l» &e" + player.getName() + "&6 has found a &aCommon &6Lucky Block!"));
        if (rarity.equalsIgnoreCase("Rare")) Bukkit.broadcastMessage(CouphUtil.color("&6&l» &e" + player.getName() + "&6 has found a &9Rare &6Lucky Block!"));
        if (rarity.equalsIgnoreCase("Legendary")) Bukkit.broadcastMessage(CouphUtil.color("&6&l» &e" + player.getName() + "&6 has found a &6Legendary &6Lucky Block!"));
        if (rarity.equalsIgnoreCase("Mythical")) Bukkit.broadcastMessage(CouphUtil.color("&6&l» &e" + player.getName() + "&6 has found a &cMythical &6Lucky Block!"));


        CouphUtil.playSound(player, Sound.ENTITY_IRONGOLEM_DEATH);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lb give " + player.getName() + " lootbox" + rarity + "luckyblock");
    }

    public boolean canBreakAtLoc(Player player, Location l) {
        Plugin plugin = WorldGuardPlugin.inst();
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(l);

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
