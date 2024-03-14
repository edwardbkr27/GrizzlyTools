package me.couph.grizzlytools.util;

import com.google.common.collect.Maps;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PickaxeUtil {
    public static Map<Enchantment, Integer> getEnchantments(GrizzlyPickaxe grizzlyPickaxe) {
        GrizzlyTools plugin = GrizzlyTools.getInstance();
        FileConfiguration config = plugin.getConfig();
        String name = grizzlyPickaxe.getName().toLowerCase();
        boolean useDefaultEnchantments = !config.contains("enchantments." + name);
        if (useDefaultEnchantments)
            return getEnchantments(GrizzlyTools.getInstance().getConfig().getStringList("enchantments.default"));
        return getEnchantments(GrizzlyTools.getInstance().getConfig().getStringList("enchantments." + name));
    }

    public static Map<Enchantment, Integer> getEnchantments(List<String> strings) {
        Map<Enchantment, Integer> enchantmentIntegerMap = Maps.newHashMap();
        for (String s : strings)
            enchantmentIntegerMap.putAll(getEnchantment(s));
        return enchantmentIntegerMap;
    }

    public static Map<Enchantment, Integer> getEnchantment(String string) {
        String[] args = string.split(":");
        Enchantment enchantment = getEnchantmentByName(args[0]);
        Integer level = Integer.parseInt(args[1]);
        return Collections.singletonMap(enchantment, level);
    }

    private static Enchantment getEnchantmentByName(String enchantment) {
        switch (enchantment.toUpperCase()) {
            case "EFFICIENCY":
                return Enchantment.DIG_SPEED;
            case "UNBREAKING":
                return Enchantment.DURABILITY;
            case "FORTUNE":
                return Enchantment.LOOT_BONUS_BLOCKS;
        }
        return Enchantment.getByName(enchantment);
    }
}
