package de.norsecraft.antishiftclick;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class AntiShiftClick extends JavaPlugin implements Listener {
    private HashMap<String, String> playerItemMap;
    private List<String> disallowedItemsList;

    public AntiShiftClick() {
    }

    public void onEnable() {
        try {
            getServer().getPluginManager().registerEvents(this, this);
            playerItemMap = new HashMap<String, String>();
            loadDisallowList();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "error in onEnable of AntiShiftClick");
            getLogger().log(Level.SEVERE, e.toString());
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent openEvent) {
        try {
            Player eventPlayer = (Player) openEvent.getPlayer();
            String eventPlayerName = eventPlayer.getName();
            String EventInventoryName = openEvent.getView().getTitle();
            playerItemMap.put(eventPlayerName, EventInventoryName);
            if (eventPlayer.hasPermission("antishiftclick.seestuff"))
                eventPlayer.sendMessage(openEvent.getView().getTitle());
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, e.toString());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent closeEvent) {
        try {
            Player eventPlayer = (Player) closeEvent.getPlayer();
            String eventPlayerName = eventPlayer.getName();
            playerItemMap.put(eventPlayerName, "-1");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, e.toString());
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent loginEvent) {
        try {
            String loginPlayerName = loginEvent.getPlayer().getName();
            playerItemMap.put(loginPlayerName, "-1");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "error in onPlayerLogin of AntiShiftClick");
            getLogger().log(Level.SEVERE, e.toString());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent logoutEvent) {
        try {
            String playerNameThatQuit = logoutEvent.getPlayer().getName();
            playerItemMap.remove(playerNameThatQuit);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "error in onPlayerQuit of AntiShiftClick");
            getLogger().log(Level.SEVERE, e.toString());
        }
    }

    @EventHandler
    public void onShiftClick(InventoryClickEvent clickEvent) {
        Player eventPlayer;
        String inventoryPlayerIsOpenIn;
        Iterator<String> iterator;
        if (!clickEvent.isShiftClick()) {
            return;
        }
        try {
            eventPlayer = (Player) clickEvent.getWhoClicked();
            inventoryPlayerIsOpenIn = playerItemMap.get(eventPlayer.getName());
            iterator = disallowedItemsList.iterator();
            while (iterator.hasNext()) {
                String disallowedItem = iterator.next();
                if (inventoryPlayerIsOpenIn.equalsIgnoreCase(disallowedItem) && !eventPlayer.hasPermission("antishiftclick.allow")) {
                    clickEvent.setCancelled(true);
                    return;
                }
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "error in onShiftClick of AntiShiftClick");
            getLogger().log(Level.SEVERE, e.toString());
        }
    }

    private void loadDisallowList() {
        File dataFolder = getDataFolder();
        File configFile = new File(dataFolder, "config.yml");
        if (!dataFolder.exists() || !configFile.exists()) {
            saveDefaultConfig();
            disallowedItemsList = getConfig().getStringList("DisallowedBlocks");
            return;
        }
        try {
            saveDefaultConfig();
            disallowedItemsList = getConfig().getStringList("DisallowedBlocks");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "error in loadDisallowList of AntiShiftClick");
            getLogger().log(Level.SEVERE, e.toString());
        }
    }
}