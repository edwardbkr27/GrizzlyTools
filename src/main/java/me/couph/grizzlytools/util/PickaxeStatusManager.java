package me.couph.grizzlytools.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import me.couph.grizzlybackpacks.GrizzlyBackpacks;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class PickaxeStatusManager {
    private final Map<String, Boolean> enabledMap;

    public PickaxeStatusManager(PickaxeHandler grizzlyPickaxeHandler) {
        this.enabledMap = Maps.newHashMap();
        grizzlyPickaxeHandler.getGrizzlyPickaxes().forEach(pickaxe -> this.enabledMap.put(pickaxe.getName(), true));
        load();
    }

    public void enable(GrizzlyPickaxe pickaxe) {
        this.enabledMap.put(pickaxe.getName(), Boolean.valueOf(true));
    }

    public void disable(GrizzlyPickaxe pickaxe) {
        this.enabledMap.put(pickaxe.getName(), Boolean.valueOf(false));
    }

    public void enableAll() {
        this.enabledMap.replaceAll((key, value) -> true);
    }

    public void disableAll() {
        this.enabledMap.replaceAll((key, value) -> false);
    }

    public boolean isEnabled(GrizzlyPickaxe pickaxe) {
        return ((Boolean)this.enabledMap.get(pickaxe.getName())).booleanValue();
    }

    public boolean isDisabled(GrizzlyPickaxe pickaxe) {
        return !((Boolean)this.enabledMap.get(pickaxe.getName())).booleanValue();
    }

    public List<String> getListDescription() {
        List<String> list = Lists.newArrayList();
        this.enabledMap.forEach((pickaxeName, bool) -> {
            GrizzlyPickaxe pickaxe = GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByName(pickaxeName);
            list.add(((pickaxe.getDisplayName() == null) ? (pickaxe.getColorBold() + pickaxe.getName()) : pickaxe.getDisplayName()) + "&r &f&l(&f" + pickaxeName + "&f&l) &8- " + (isEnabled(pickaxe) ? "&a&lENABLED" : "&c&lDISABLED"));
        });
        return MoreUtil.color(list);
    }

    public void load() {
        try {
            Gson gson = new Gson();
            Type type = (new TypeToken<Map<String, Boolean>>() {

            }).getType();
            FileReader fileReader = new FileReader(getFile());
            Map<String, Boolean> enabledMap = (Map<String, Boolean>)gson.fromJson(fileReader, type);
            this.enabledMap.putAll(enabledMap);
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        try {
            File file = new File(GrizzlyTools.getInstance().getDataFolder() + File.separator + "pickaxestatus.json");
            if (!file.exists())
                file.createNewFile();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void save() {
        try {
            (new FileWriter(getFile(), false)).close();
            FileWriter fileWriter = new FileWriter(getFile());
            Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(gson.toJson(this.enabledMap));
            fileWriter.write(gson.toJson(jsonElement));
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



