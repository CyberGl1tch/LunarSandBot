package me.magas8.Hooks.FactionsHooks;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Role;
import me.magas8.Hooks.FactionHook;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FactionsSaberMassive extends FactionHook {

    @Override
    public Boolean isFactionAdmin(Player player) {
//todo see how can i use saber and UUID same jar
        if(FPlayers.getInstance().getByPlayer(player).getRole().equals(Role.valueOf("COLEADER")) || FPlayers.getInstance().getByPlayer(player).getRole().equals(Role.valueOf("MODERATOR")) || FPlayers.getInstance().getByPlayer(player).getRole().equals(Role.valueOf("LEADER"))){
            return true;
        }
        return false;
    }
    @Override
    public String getFactionTag(Player player) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer.getFaction() == null)
            return null;
        return fPlayer.getFaction().getTag();
    }
    @Override
    public String getFactionTag(OfflinePlayer player) {
        FPlayer fPlayer = FPlayers.getInstance().getByOfflinePlayer(player);
        if (fPlayer.getFaction() == null)
            return null;
        return fPlayer.getFaction().getTag();
    }

    @Override
    public String getFactionTagFromId(String id) {
        try {
            return Factions.getInstance().getFactionById(id).getTag();
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public String getFactionId(Player player) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer.getFaction() == null)
            return null;
        if (!fPlayer.getFaction().isNormal())
            return null;
        return fPlayer.getFaction().getId();
    }

    @Override
    public String getFactionIdAtLocation(Location loc) {
        FLocation floc = new FLocation(loc);
        Faction locfaction = Board.getInstance().getFactionAt(floc);
        if(locfaction==null) return null;
        if(!locfaction.isNormal()) return null;
        return locfaction.getId();
    }
}
