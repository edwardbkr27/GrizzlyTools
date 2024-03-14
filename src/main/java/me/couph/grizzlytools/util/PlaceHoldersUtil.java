package me.couph.grizzlytools.util;

import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import me.couph.grizzlytools.util.CouphUtil;
import java.util.List;
import java.util.stream.Collectors;

public class PlaceHoldersUtil {
    public static String translatePlaceholders(String message, GrizzlyPickaxe grizzlyPickaxe) {
        return getDefaultProperties().format(message, grizzlyPickaxe);
    }

    public static String translatePlaceholders(String message, GrizzlyPickaxe grizzlyPickaxe, PlaceholderProperties properties) {
        return properties.format(message, grizzlyPickaxe);
    }

    public static List<String> translatePlaceholders(List<String> messages, GrizzlyPickaxe grizzlyPickaxe) {
        return (List<String>)messages.stream().map(msg -> translatePlaceholders(msg, grizzlyPickaxe)).collect(Collectors.toList());
    }

    public static List<String> translatePlaceholders(List<String> messages, GrizzlyPickaxe grizzlyPickaxe, PlaceholderProperties properties) {
        return (List<String>)messages.stream().map(msg -> translatePlaceholders(msg, grizzlyPickaxe, properties)).collect(Collectors.toList());
    }

    public static PlaceholderProperties getDefaultProperties() {
        PlaceholderProperties properties = new PlaceholderProperties();
        properties.put("%color%", grizzlyPickaxe -> CouphUtil.color(String.valueOf(grizzlyPickaxe.getColor())));
        properties.put("%colorB%", grizzlyPickaxe -> grizzlyPickaxe.getColorBold());
        properties.put("%grizzlypickaxe%", grizzlyPickaxe -> grizzlyPickaxe.getName());
        properties.put("%grizzlypickaxeU%", grizzlyPickaxe -> grizzlyPickaxe.getName().toUpperCase());
        properties.put("%grizzlypickaxeL%", grizzlyPickaxe -> grizzlyPickaxe.getName().toLowerCase());
        properties.put("%identifier%", grizzlyPickaxe -> grizzlyPickaxe.getPickaxeIdentifier());
        properties.put("%displayname%", grizzlyPickaxe -> (grizzlyPickaxe.getDisplayName() == null) ? grizzlyPickaxe.getName() : grizzlyPickaxe.getDisplayName());
        properties.put("%displaynameU%", grizzlyPickaxe -> (grizzlyPickaxe.getDisplayName() == null) ? grizzlyPickaxe.getName().toUpperCase() : grizzlyPickaxe.getDisplayName().toUpperCase());
        return properties;
    }
}
