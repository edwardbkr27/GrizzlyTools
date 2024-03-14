package me.couph.grizzlytools.util;

import me.couph.grizzlybackpacks.GrizzlyBackpacks;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreditHandler {
    private final GrizzlyTools plugin;

    public GrizzlyTools getPlugin() {
        return this.plugin;
    }
    private HashMap<UUID, HashMap<String, Integer>> playerData;
    private final File dataFile;

    public CreditHandler(GrizzlyTools plugin) {
        this.plugin = plugin;
        playerData = new HashMap<>();
        dataFile = new File(GrizzlyTools.getInstance().getDataFolder() + File.separator + "creditdata.txt");
        loadPlayerData();
    }

    public void addCredit(UUID uuid, String pickaxeType) {
        setPlayerData(uuid, pickaxeType, getPickaxeCredit(uuid, pickaxeType)+1);
    }

    public void removeCredit(UUID uuid, String pickaxeType) {
        setPlayerData(uuid, pickaxeType, getPickaxeCredit(uuid, pickaxeType)-1);
    }

    public boolean hasCredit(UUID uuid, String pickaxeType) {
        if (getPickaxeCredit(uuid, pickaxeType) > 0) return true;
        return false;
    }

    public void setPlayerData(UUID uuid, String pickaxeType, int amount) {
        HashMap<String, Integer> items = playerData.getOrDefault(uuid, new HashMap<>());
        items.put(pickaxeType, amount);
        playerData.put(uuid, items);
        savePlayerData();
    }

    public int getPickaxeCredit(UUID uuid, String pickaxeType) {
        HashMap<String, Integer> items = playerData.get(uuid);
        if (items != null) {
            return items.getOrDefault(pickaxeType, 0);
        }
        return 0;
    }


//    public void refreshCreditByPlayer(UUID uuid) {
//        String targetLine = uuid.toString() + ":";
//        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                if (line.startsWith(targetLine)) {
//                    String[] parts = line.split(":");
//
//                }
//            }
//        } catch (IOException e) {
//            // Handle any IO errors
//            e.printStackTrace();
//        }
//    }

    public void refreshCreditByPlayer(UUID uuid) {
        String targetLine = uuid.toString() + ":";
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            HashMap<String, Integer> items = new HashMap<>();;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(targetLine)) {
                    String[] parts = line.split(":");
                    String pickaxeType = parts[1];
                    int amount = Integer.parseInt(parts[2]);
                    items.put(pickaxeType, amount);
                }
            }
            playerData.put(uuid, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void loadPlayerData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 3) {
                    try {
                        UUID uuid = UUID.fromString(parts[0]);
                        String pickaxeType = parts[1];
                        int amount = Integer.parseInt(parts[2]);

                        HashMap<String, Integer> items = (HashMap<String, Integer>) playerData.getOrDefault(uuid, new HashMap<>());
                        items.put(pickaxeType, amount);
                        playerData.put(uuid, items);
                    } catch (IllegalArgumentException ignored) {
                        // Ignore invalid UUIDs or integers
                    }
                }
            }
        } catch (IOException e) {
            // Handle any IO errors
            e.printStackTrace();
        }
    }

    private void savePlayerData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (Map.Entry<UUID, HashMap<String, Integer>> entry : playerData.entrySet()) {
                UUID uuid = entry.getKey();
                Map<String, Integer> items = entry.getValue();
                for (Map.Entry<String, Integer> itemEntry : items.entrySet()) {
                    String pickaxeType = itemEntry.getKey();
                    int amount = itemEntry.getValue();
                    writer.write(uuid.toString() + ":" + pickaxeType + ":" + amount);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
