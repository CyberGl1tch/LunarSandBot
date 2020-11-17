package me.magas8.Hooks;

import me.magas8.Hooks.FactionsHooks.*;
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
    public static String realName = "Factions";
    @Override
    public FactionHook setup(LunarSandBot pluginInstance) {
        if (Bukkit.getServer().getPluginManager().getPlugin("FactionsX") !=null) {
            hookName = "FactionsX";
            realName = "FactionsX";
            return (FactionHook)new FactionsX(pluginInstance);
        }
        if (Bukkit.getServer().getPluginManager().getPlugin("MassiveCore") !=null) {
            hookName = "MassiveCoreFactions";
            realName = "Factions";
            return (FactionHook)new MassiveCoreFactions(pluginInstance);
        }
        try {
            plugin = Bukkit.getServer().getPluginManager().getPlugin(getHookName());
        } catch (NullPointerException nullPointerException) {
            return null;
        }
        if (plugin!=null && plugin.getDescription().getAuthors().contains("Savag3life")) {
            hookName = "SupremeFactions";
            realName = "Factions";
            return (FactionHook)new SupremeFactions(pluginInstance);
        }
        if (plugin!=null && !plugin.getDescription().getAuthors().contains("ProSavage") && (plugin.getDescription().getAuthors().contains("drtshock") || plugin.getDescription().getAuthors().contains("Benzimmer"))) {
            hookName = "FactionsUUID";
            realName = "Factions";
            return (FactionHook)new FactionsUuid(pluginInstance);
        }
        return (FactionHook) new SaberFactions(pluginInstance);
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

    @Override
    public String getHookPluginName() { return realName; }
}
