package me.magas8.Hooks;

import me.magas8.Hooks.FactionsHooks.BeastFactions;
import me.magas8.Hooks.FactionsHooks.FactionsSaberMassive;
import me.magas8.Hooks.FactionsHooks.FactionsUUID;
import me.magas8.Hooks.FactionsHooks.FactionsX;
import me.magas8.LunarSandBot;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class FactionHook implements PluginHooks<FactionHook> {
    private Plugin plugin;
    public static String hookName = "Factions";
    @Override
    public FactionHook setup() {
        if (Bukkit.getPluginManager().isPluginEnabled("FactionsX")) {
            hookName = "FactionsX";
            Bukkit.getConsoleSender().sendMessage("Not Supported yet "+hookName);
            //return (FactionHook)new FactionsX();
        }
        if (Bukkit.getPluginManager().isPluginEnabled("BeastFactions")) {
            hookName = "BeastFactions";
            Bukkit.getConsoleSender().sendMessage("Not Supported yet "+hookName);
            //return (FactionHook)new BeastFactions();
        }
        try {
            plugin = Bukkit.getServer().getPluginManager().getPlugin(getHookName());
        } catch (NullPointerException nullPointerException) {
            return null;
        }
        if (!plugin.getDescription().getAuthors().contains("ProSavage") && (plugin.getDescription().getAuthors().contains("drtshock") || plugin.getDescription().getAuthors().contains("Benzimmer"))) {
            hookName = "FactionsUUID";
            Bukkit.getConsoleSender().sendMessage("Successfully hooked "+hookName);
            return (FactionHook)new FactionsUUID();
        }
        Bukkit.getConsoleSender().sendMessage("Successfully hooked "+hookName);
        return (FactionHook) new FactionsSaberMassive();
    }
    public String getFactionTag(Player player) {
        throw new NotImplementedException("Server Faction plugin is not supported contact with dev");
    }
    public String getFactionTag(OfflinePlayer player) {
        throw new NotImplementedException("Server Faction plugin is not supported contact with dev");
    }
    public String getFactionTagFromId(String id) {
        throw new NotImplementedException("Server Faction plugin is not supported contact with dev");
    }

    public String getFactionId(Player player) {
        throw new NotImplementedException("Server Faction plugin is not supported contact with dev");
    }
    public Boolean isFactionAdmin(Player player) {
        throw new NotImplementedException("Server Faction plugin is not supported contact with dev");
    }
    public String getFactionIdAtLocation(Location loc) {
        throw new NotImplementedException("Server Faction plugin is not supported contact with dev");
    }
    @Override
    public String getHookName() {
        return hookName;
    }
}
