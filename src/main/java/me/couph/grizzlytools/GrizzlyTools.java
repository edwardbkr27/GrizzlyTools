package me.couph.grizzlytools;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import me.couph.grizzlytools.Commands.PickaxeCommand;
import me.couph.grizzlytools.Commands.PickaxeGuiCommand;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import me.couph.grizzlytools.Items.PolarPickaxe;
import me.couph.grizzlytools.Listeners.GrizzlyPickaxeListener;
import me.couph.grizzlytools.util.CreditHandler;
import me.couph.grizzlytools.util.GuiUtil;
import me.couph.grizzlytools.util.PickaxeHandler;
import me.couph.grizzlytools.util.PlayerMapHandler;
import org.bukkit.Bukkit;
import me.couph.grizzlybackpacks.*;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class GrizzlyTools extends JavaPlugin implements Listener {
    private static GrizzlyTools instance;
    private PickaxeHandler grizzlyPickaxeHandler;
    private CreditHandler creditHandler;
    public static GrizzlyTools getInstance() {
        return instance;
    }
    public PickaxeHandler getGrizzlyPickaxeHandler() {
        return this.grizzlyPickaxeHandler;
    }
    public CreditHandler getCreditHandler() {
        return this.creditHandler;
    }

    public PlayerMapHandler playerMapHandler;
    public static WorldGuardPlugin worldGuard;
    public static WorldEditPlugin worldEdit;
    public GrizzlyBackpacks grizzlyBackpacks;
    public void onEnable() {
        instance = this;
        new PickaxeGuiCommand();
        new PickaxeCommand();
        this.grizzlyPickaxeHandler = new PickaxeHandler(this);
        this.creditHandler = new CreditHandler(this);
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        worldGuard = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldGuard == null) {
            getLogger().severe("WorldGuard plugin not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null) {
            getLogger().severe("WorldEdit plugin not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        grizzlyBackpacks = (GrizzlyBackpacks) getServer().getPluginManager().getPlugin("GrizzlyBackpacks");
        if (grizzlyBackpacks == null) {
            getLogger().severe("GrizzlyBackpacks plugin not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents((Listener)new GrizzlyPickaxeListener(this), (Plugin)this);
        getServer().getPluginManager().registerEvents(this, (Plugin)this);
        getServer().getPluginManager().registerEvents((Listener)new GuiUtil(this, getCreditHandler()), (Plugin)this);
        getServer().getPluginManager().registerEvents((Listener)new PolarPickaxe(), this);
        this.playerMapHandler = new PlayerMapHandler(this);
        this.playerMapHandler.setPlayerMap();
        getServer().getPluginManager().registerEvents(this.playerMapHandler, this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (GrizzlyPickaxe grizzlyPickaxe : getGrizzlyPickaxeHandler().getGrizzlyPickaxes()) {
                if (grizzlyPickaxe.isGrizzlyPickaxe(player.getInventory().getItemInMainHand()))
                    getGrizzlyPickaxeHandler().setActivePickaxe(player, grizzlyPickaxe);
            }
        }
    }

    public void reloadAll() {
        reloadConfig();
        this.grizzlyPickaxeHandler.registerGrizzlyPickaxes();
    }

    public void onDisable() {
        this.grizzlyPickaxeHandler.getPickaxeStatusManager().save();
    }
}
