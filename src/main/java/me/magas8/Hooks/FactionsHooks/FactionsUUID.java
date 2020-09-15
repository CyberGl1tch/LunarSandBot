package me.magas8.Hooks.FactionsHooks;

import me.magas8.library.FactionsUUID;
import me.magas8.Hooks.FactionHook;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FactionsUuid extends FactionHook {
    @Override
    public Boolean isFactionAdmin(Player player) {
        return new FactionsUUID().isFactionAdmin(player);
    }


    @Override
    public String getFactionTag(Player player) {
        return new FactionsUUID().getFactionTag(player);
    }
    @Override
    public String getFactionTag(OfflinePlayer player) {
        return new FactionsUUID().getFactionTag(player);
    }

    @Override
    public String getFactionTagFromId(String id) {
        return new FactionsUUID().getFactionTagFromId(id);
    }

    @Override
    public String getFactionId(Player player) {
        return new FactionsUUID().getFactionId(player);
    }


    @Override
    public String getFactionIdAtLocation(Location loc) {
        return new FactionsUUID().getFactionIdAtLocation(loc);
    }
}
