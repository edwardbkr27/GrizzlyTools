package me.couph.grizzlytools.util;

import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GuiUtil implements Listener {
    public final GrizzlyTools plugin;
    private static CreditHandler creditHandler;

    public GuiUtil(GrizzlyTools plugin, CreditHandler creditHandler) {
        this.plugin = plugin;
        this.creditHandler = creditHandler;
    }

    public static void openGui(Player player, List<ItemStack> pickaxes) {
        Inventory gui = Bukkit.createInventory(null, 18, "Pickaxes GUI");
        int count=0;
        // add implementation for player credits
        for (ItemStack pickaxe : pickaxes) {
            String pickaxeName = CouphUtil.colorAndStrip(pickaxe.getItemMeta().getDisplayName());
            String[] words = pickaxeName.split(" ");
            String pickaxeType = words[0];
            int credit = creditHandler.getPickaxeCredit(player.getUniqueId(), pickaxeType);
            ItemMeta meta = pickaxe.getItemMeta();
            List<String> currLore = meta.getLore();
            currLore.add("");
            //currLore.add(CouphUtil.color("&e&lAVAILABLE CREDITS:&r &f&l" + Integer.toString(credit)));
            //currLore.add("");
            currLore.add(CouphUtil.color("&c(!) Obtain these items through in-game rewards or"));
            currLore.add(CouphUtil.color("&cpurchase them on our store: buy.grizzlyprison.com!"));
            meta.setLore(currLore);
            pickaxe.setItemMeta(meta);
            gui.setItem(count, pickaxe);
            count++;
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getInventory().getTitle().equalsIgnoreCase("Pickaxes GUI")) return;
        event.setCancelled(true);
        final ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        final Player player = (Player) event.getWhoClicked();
        // add credit implementation
        int clickedPickaxeSlot = event.getRawSlot();
        if (!(clickedPickaxeSlot < 9)) return;
        ItemStack clickedPickaxe = event.getInventory().getItem(clickedPickaxeSlot);
        String pickName = CouphUtil.colorAndStrip(clickedPickaxe.getItemMeta().getDisplayName());
        String[] words = pickName.split(" ");
        String pickType = words[0];
        int credit = creditHandler.getPickaxeCredit(player.getUniqueId(), pickType);
        if (credit>0) {
            if (CouphUtil.isInventoryFull(player)) {
                player.sendMessage(CouphUtil.color("&c(!) Please create a space in your inventory!"));
                return;
            }
            GrizzlyPickaxe pickaxe = GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(clickedPickaxe);
            player.getInventory().addItem(pickaxe.createPickaxe(Material.DIAMOND_PICKAXE));
            player.sendMessage(CouphUtil.color("&a(!) You have successfully redeemed a pickaxe!"));
            player.sendMessage(CouphUtil.color("&c(-1 Pickaxe Credit)"));
            CouphUtil.playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
            creditHandler.removeCredit(player.getUniqueId(), pickType);
            player.getOpenInventory().close();
        } else {
            player.sendMessage(CouphUtil.color("&c(!) You do not have enough credits for this!"));
            player.sendMessage(CouphUtil.color("&c(!) Receive credits through in-game rewards or buy.grizzlyprison.com"));
            player.getOpenInventory().close();
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent event) {
        if (event.getInventory().getTitle().equalsIgnoreCase("Pickaxes GUI")) {
            event.setCancelled(true);
        }
    }
}
