package me.couph.grizzlytools.Commands;

import me.couph.grizzlytools.GrizzlyTools;
import org.bukkit.Bukkit;
import me.couph.grizzlytools.util.SimpleCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PickaxeGuiCommand extends SimpleCommand implements Listener {
    public PickaxeGuiCommand() {
        super("pickaxes", "grizzlytools.gui", "Grizzly pickaxes gui command.", "/pickaxes",
                Boolean.valueOf(false), Integer.valueOf(0), (JavaPlugin) GrizzlyTools.getInstance());
        Bukkit.getPluginManager().registerEvents(this, (Plugin) getPlugin());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        GrizzlyTools plugin = (GrizzlyTools) getPlugin();
        if (!(sender instanceof Player)) return false;
        plugin.getCreditHandler().refreshCreditByPlayer(((Player) sender).getUniqueId());
        ((GrizzlyTools) getPlugin()).getGrizzlyPickaxeHandler().getGui((Player) sender);
        return true;
    }
}
