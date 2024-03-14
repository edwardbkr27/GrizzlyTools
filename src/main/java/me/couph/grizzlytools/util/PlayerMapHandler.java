package me.couph.grizzlytools.util;

import me.couph.grizzlytools.GrizzlyTools;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMapHandler implements Listener {

    private GrizzlyTools plugin;

    private Map<String, String> playerMap;
    private File dataFile;

    public PlayerMapHandler(GrizzlyTools plugin) {
        this.plugin = plugin;
        dataFile = new File(GrizzlyTools.getInstance().getDataFolder() + File.separator + "playermap.txt");
        playerMap = new HashMap<>();
    }

    public void setPlayerMap() {
        try (BufferedReader br = new BufferedReader(new FileReader(GrizzlyTools.getInstance().getDataFolder() + File.separator + "playermap.txt"))) {
            String line;

            // Read each line of the file
            while ((line = br.readLine()) != null) {
                // Split the line using ":" as the separator
                String[] parts = line.split(":");

                if (parts.length == 2) {
                    // Trim any leading/trailing spaces and store the data in the map
                    String playerName = parts[0].trim();
                    String playerUUID = parts[1].trim();

                    playerMap.put(playerName, playerUUID);
                } else {
                    System.out.println("Invalid line: " + line);
                }
            }
        } catch (Exception e) {
            return;
        }
    }

    public void addPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        playerMap.put(player.getName().toUpperCase(), String.valueOf(uuid));
        writeToFile();
    }

    public String getUUID(String playerName) {
        setPlayerMap();
        return playerMap.getOrDefault(playerName.toUpperCase(), null);
    }

    public boolean checkForPlayer(Player player) {
        setPlayerMap();
        return playerMap.containsKey(player.getName().toUpperCase());
    }

    public void writeToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(GrizzlyTools.getInstance().getDataFolder() + File.separator + "playermap.txt"))) {
            for (Map.Entry<String, String> entry : playerMap.entrySet()) {
                String playerName = entry.getKey();
                String playerUUID = entry.getValue();
                String line = playerName + ":" + playerUUID;

                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (checkForPlayer(event.getPlayer())) return;
        addPlayer(event.getPlayer());
    }
}
