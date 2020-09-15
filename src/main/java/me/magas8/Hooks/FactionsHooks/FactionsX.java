package me.magas8.Hooks.FactionsHooks;

import me.magas8.Hooks.FactionHook;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.FactionManager;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.manager.PlayerManager;
import net.prosavage.factionsx.util.MemberAction;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FactionsX extends FactionHook {
    @Override
    public Boolean isFactionAdmin(Player player) {
        if (PlayerManager.INSTANCE.getFPlayer(player).getFaction().isWilderness() ||PlayerManager.INSTANCE.getFPlayer(player).getFaction().isSafezone() || PlayerManager.INSTANCE.getFPlayer(player).getFaction().isWarzone() )
            return false;
        if(PlayerManager.INSTANCE.getFPlayer(player).isLeader() || PlayerManager.INSTANCE.getFPlayer(player).getRole().getAllowedMemberActions().contains(MemberAction.CLAIM) ||PlayerManager.INSTANCE.getFPlayer(player).getRole().getAllowedMemberActions().contains(MemberAction.UNCLAIM) ||PlayerManager.INSTANCE.getFPlayer(player).getRole().getAllowedMemberActions().contains(MemberAction.PROMOTE)){
            return true;
        }
        return false;
    }


    @Override
    public String getFactionTag(Player player) {
        if (PlayerManager.INSTANCE.getFPlayer(player).getFaction().isWilderness() ||PlayerManager.INSTANCE.getFPlayer(player).getFaction().isSafezone() || PlayerManager.INSTANCE.getFPlayer(player).getFaction().isWarzone() ) return null;
        return PlayerManager.INSTANCE.getFPlayer(player).getFaction().getTag();
    }
    @Override
    public String getFactionTag(OfflinePlayer player) {
        try {
            if (PlayerManager.INSTANCE.getFPlayer(player.getUniqueId()).getFaction().isWilderness() || PlayerManager.INSTANCE.getFPlayer(player.getUniqueId()).getFaction().isSafezone() || PlayerManager.INSTANCE.getFPlayer(player.getUniqueId()).getFaction().isWarzone())
                return null;
            return PlayerManager.INSTANCE.getFPlayer(player.getUniqueId()).getFaction().getTag();
        }catch (NullPointerException npe){
         return null;
        }
    }

    @Override
    public String getFactionTagFromId(String id) {
        try {
            return FactionManager.INSTANCE.getFaction(Long.parseLong(id)).getTag();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    @Override
    public String getFactionId(Player player) {
        if (PlayerManager.INSTANCE.getFPlayer(player).getFaction().isWilderness() ||PlayerManager.INSTANCE.getFPlayer(player).getFaction().isSafezone() || PlayerManager.INSTANCE.getFPlayer(player).getFaction().isWarzone() ) return null;
        return PlayerManager.INSTANCE.getFPlayer(player).getFaction().getId() + "";
    }


    @Override
    public String getFactionIdAtLocation(Location loc) {
        Faction locfaction = GridManager.INSTANCE.getFactionAt(loc.getChunk());
        if(locfaction.isSystemFaction()) return null;
        if(locfaction.isSafezone() || locfaction.isWarzone() || locfaction.isWilderness()) return null;
        return String.valueOf(locfaction.getId());
    }
}
