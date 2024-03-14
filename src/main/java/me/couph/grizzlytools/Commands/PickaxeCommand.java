package me.couph.grizzlytools.Commands;

import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import me.couph.grizzlytools.util.CreditHandler;
import me.couph.grizzlytools.util.MoreUtil;
import me.couph.grizzlytools.util.CouphUtil;
import me.couph.grizzlytools.util.SimpleCommand;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PickaxeCommand extends SimpleCommand implements Listener {
    public PickaxeCommand() {
        super("gt", "grizzlytools.admin", "Grizzly tools command.", "/gt",
                Boolean.valueOf(false), Integer.valueOf(0), (JavaPlugin)GrizzlyTools.getInstance());
        Bukkit.getPluginManager().registerEvents(this, (Plugin)getPlugin());
    }

    public boolean execute(CommandSender sender, String[] args) {
        GrizzlyTools plugin = (GrizzlyTools)getPlugin();
        if (args.length == 0) {
            sendHelp(sender);
            return false;
        }
        if (args[0].equalsIgnoreCase("config")) {
            if (args.length >= 2 && args[1].equalsIgnoreCase("reload")) {
                //plugin.getConfigManager().reload();
                MoreUtil.sendMessage(sender, "&a&l(&f!&a&l) &aReloaded configuration.");
                return false;
            }
            if (sender instanceof Player)
                //plugin.getConfigManager().getGUI().open((Player)sender);
            return false;
        }
        if (args[0].equalsIgnoreCase("setlevel")) {
            try {
                int level = Integer.parseInt(args[1]);
                Player player = sender.getServer().getPlayer(sender.getName());
                ItemStack item = player.getInventory().getItemInMainHand();
                GrizzlyPickaxe pickaxe = GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(item);
                if (pickaxe == null) {
                    throw new Exception("Item is not special pickaxe.");
                }
                GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().setPickaxeLevel(item, level);
                sender.sendMessage(CouphUtil.color("&a(!) You have set the pickaxe level to " + level + "."));
                return false;
            } catch (Exception e) {
                sender.sendMessage(CouphUtil.color("&c(!) /gt setlevel <level> - whilst holding pickaxe."));
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("give")) {
            try {
                if (args.length < 2) {
                    sender.sendMessage(CouphUtil.color("&c(!) Usage: /gt give <player> <tool>"));
                    return false;
                }
                Player player = Bukkit.getPlayer(args[1]);
                GrizzlyPickaxe grizzlyPickaxe = plugin.getGrizzlyPickaxeHandler().getByName(args[2]);
                if (player == null) {
                    sender.sendMessage(CouphUtil.color("&c&l(!)&c Player not found, &7[" + args[1] + "]"));
                    return false;
                }
                if (grizzlyPickaxe == null) {
                    sender.sendMessage(CouphUtil.color("&c&l(!)&c Pickaxe not found, &7[" + args[2] + "]"));
                    return false;
                }
                ItemStack item = grizzlyPickaxe.createPickaxe(Material.DIAMOND_PICKAXE);
                if (!CouphUtil.isInventoryFull(player)) {
                    player.getInventory().addItem(item);
                    sender.sendMessage(CouphUtil.color("&a&l(!)&a You have given a " + args[2] + " pickaxe to " + player.getName()));
                    player.sendMessage(CouphUtil.color("&a&l(!)&a You have been given a " + args[2] + " pickaxe by " + sender.getName()));
                    return false;
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gt givecredit " + player.getName() + " " + args[2]);
                    player.sendMessage(CouphUtil.color("&a(!) You were given a /pickaxes credit!"));
                    sender.sendMessage(CouphUtil.color("&c&l(!)&c Players inventory is full!"));
                    return false;
                }
            } catch(Exception e) {
                sender.sendMessage(CouphUtil.color("&c(!) Usage: /gt give <player> <tool>"));
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("givecredit")) {
            try {
                CreditHandler creditHandler = new CreditHandler((GrizzlyTools)getPlugin());
                if (!(sender instanceof ConsoleCommandSender)) {
                    sender.sendMessage(CouphUtil.color("&c(!) Only console can use this command!"));
                }
                if (args.length < 2) {
                    MoreUtil.sendMessage(sender, "&c&l(!) &cUsage: /gt givecredit <name> <pickaxeType>");
                    return false;
                }
                UUID uuid = UUID.fromString(plugin.playerMapHandler.getUUID(args[1].toUpperCase()));
                String pickaxeType = args[2];
                creditHandler.addCredit(uuid, pickaxeType);
                sender.sendMessage(CouphUtil.color("&a(!) Credit added!"));
                return true;
            } catch(Exception e) {
                sender.sendMessage(CouphUtil.color("&c(!) There was an error executing this command."));
            }
        }
        if (args[0].equalsIgnoreCase("removecredit")) {
            try {
                CreditHandler creditHandler = new CreditHandler((GrizzlyTools)getPlugin());
                if (!(sender instanceof ConsoleCommandSender)) {
                    sender.sendMessage(CouphUtil.color("&c(!) Only console can use this command!"));
                }
                if (args.length < 2) {
                    MoreUtil.sendMessage(sender, "&c&l(!) &cUsage: /gt removecredit <name> <pickaxeType>");
                    return false;
                }
                UUID uuid = UUID.fromString(plugin.playerMapHandler.getUUID(args[1].toUpperCase()));
                String pickaxeType = args[2];
                creditHandler.removeCredit(uuid, pickaxeType);
                sender.sendMessage(CouphUtil.color("&a(!) Credit removed!"));
                return true;

            } catch(Exception e) {
                sender.sendMessage(CouphUtil.color("&c(!) There was an error executing this command."));
            }
        }
        if (args[0].equalsIgnoreCase("list")) {
            plugin.getGrizzlyPickaxeHandler().getPickaxeStatusManager().getListDescription().forEach(message -> MoreUtil.sendMessage(sender, message));
            return false;
        }
        if (args[0].equalsIgnoreCase("enable")) {
            if (args.length < 2) {
                MoreUtil.sendMessage(sender, "&c&l(!) &cUsage: /pickaxe enable <pickaxe>");
                return false;
            }
            String pickaxeName = args[1];
            if (pickaxeName.equalsIgnoreCase("*")) {
                GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getPickaxeStatusManager().enableAll();
                MoreUtil.sendMessage(sender, "&a&l(&f!&a&l) &aEnabled all pickaxes.");
                return false;
            }
            GrizzlyPickaxe grizzlyPickaxe = GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByName(pickaxeName);
            if (grizzlyPickaxe == null) {
                MoreUtil.sendMessage(sender, "&c&l(!)&c Pickaxe not found, &7[" + pickaxeName + "]");
                return false;
            }
            GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getPickaxeStatusManager().enable(grizzlyPickaxe);
            MoreUtil.sendMessage(sender, "&a&l(&f!&a&l) &aEnabled pickaxe &e" + grizzlyPickaxe.getName());
            return false;
        }
        if (args[0].equalsIgnoreCase("disable")) {
            if (args.length < 2) {
                MoreUtil.sendMessage(sender, "&c&l(!) &cUsage: /pickaxe disable <set>");
                return false;
            }
            String pickaxeName = args[1];
            if (pickaxeName.equalsIgnoreCase("*")) {
                GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getPickaxeStatusManager().disableAll();
                MoreUtil.sendMessage(sender, "&a&l(&f!&a&l) &cDisabled &aall pickaxes.");
                GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().recalculatePickaxeStatuses();
                return false;
            }
            GrizzlyPickaxe grizzlyPickaxe = GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByName(pickaxeName);
            if (grizzlyPickaxe == null) {
                MoreUtil.sendMessage(sender, "&c&l(!)&c Pickaxe not found, &7[" + pickaxeName + "]");
                return false;
            }
            GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getPickaxeStatusManager().disable(grizzlyPickaxe);
            MoreUtil.sendMessage(sender, "&a&l(&f!&a&l) &cDisabled &apickaxe &e" + grizzlyPickaxe.getName());
            GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().recalculatePickaxeStatuses();
            return false;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadAll();
            sender.sendMessage(CouphUtil.color("&e&l(!)&e You have reloaded the Pickaxes."));
            return false;
        }
        sendHelp(sender);
        return false;
    }

    public void sendHelp(CommandSender sender) {
        MoreUtil.sendMessage(sender, Arrays.asList(new String[] { "&3&m-----------------&b&lGrizzly&3&lTools&3&m-----------------", "&b/gt &8- &fMain command.", "&b/gt list &8- &fView all pickaxes and their statuses.", "&b/gt give <player> <tool> &8- &fGive a pickaxe.", "&b/pickaxes &8- &fView available pickaxes in GUI", "&3&m----------------------------------------------------" }));
    }

}
