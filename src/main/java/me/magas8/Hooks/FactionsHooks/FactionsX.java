package me.magas8.Hooks.FactionsHooks;

import me.magas8.Hooks.FactionHook;
import me.magas8.LunarSandBot;
import me.magas8.Managers.ItemBuilder;
import me.magas8.Managers.SandBot;
import me.magas8.Utils.utils;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.event.FactionDisbandEvent;
import net.prosavage.factionsx.event.FactionUnClaimAllEvent;
import net.prosavage.factionsx.event.FactionUnClaimEvent;
import net.prosavage.factionsx.manager.FactionManager;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.manager.PlayerManager;
import net.prosavage.factionsx.util.MemberAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class FactionsX extends FactionHook implements Listener {
    private LunarSandBot plugin;
    public FactionsX(LunarSandBot plugin){
        this.plugin=plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
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
    @EventHandler
    public void onFactionDisband(FactionDisbandEvent event) {
        Player player = event.getFPlayer().getPlayer();
        int counter = 0;
        for (Iterator<SandBot> iterator = LunarSandBot.sandBots.iterator(); iterator.hasNext();) {
            SandBot bot = iterator.next();
            if(bot.getFactionID()!=null &&bot.getFactionID().equals(String.valueOf((int)event.getFaction().getId()))){
                utils.removeBot(bot);
                ItemStack botItem = new ItemBuilder(Material.valueOf(plugin.getConfig().getString("bot-spawn-item-material").toUpperCase())).setColoredName(plugin.getConfig().getString("bot-spawn-item-name")).setColoredLore(plugin.getConfig().getStringList("bot-spawn-item-lore")).toItemStack();
                botItem.setAmount(1);
                player.getInventory().addItem(botItem);
                Double cash = bot.getBalance();
                if(cash > 0){
                    LunarSandBot.econ.depositPlayer(player,cash);
                    player.sendMessage(utils.color(plugin.getConfig().getString("cash-back").replace("%amount%",cash.toString())));
                }
                counter++;
            }
        }
        player.sendMessage(utils.color(plugin.getConfig().getString("f-disband").replace("%amount%",String.valueOf(counter))));
    }

    @EventHandler
    public void onFactionUnclaimEvent(FactionUnClaimAllEvent event){
        if (event.isCancelled()) return;
        Player player = event.getFplayer().getPlayer();
        int counter = 0;
        for (Iterator<SandBot> iterator = LunarSandBot.sandBots.iterator(); iterator.hasNext();) {
            SandBot bot = iterator.next();
            if(bot.getFactionID()!=null &&bot.getFactionID().equals(String.valueOf((int)event.getUnclaimingFaction().getId()))){
                utils.removeBot(bot);
                ItemStack botItem = new ItemBuilder(Material.valueOf(plugin.getConfig().getString("bot-spawn-item-material").toUpperCase())).setColoredName(plugin.getConfig().getString("bot-spawn-item-name")).setColoredLore(plugin.getConfig().getStringList("bot-spawn-item-lore")).toItemStack();
                botItem.setAmount(1);
                player.getInventory().addItem(botItem);
                Double cash = bot.getBalance();
                if(cash > 0){
                    LunarSandBot.econ.depositPlayer(player,cash);
                    player.sendMessage(utils.color(plugin.getConfig().getString("cash-back").replace("%amount%",cash.toString())));
                }
                counter++;
            }
        }
        player.sendMessage(utils.color(plugin.getConfig().getString("f-unclaim").replace("%amount%",String.valueOf(counter))));

    }
    @EventHandler
    public void onFactionUnclaimEvent(FactionUnClaimEvent event){
        if (event.isCancelled()) return;
        Player player = event.getFplayer().getPlayer();
        Bukkit.getScheduler().runTask(plugin,()->{
            int counter = 0;
            for (Iterator<SandBot> iterator = LunarSandBot.sandBots.iterator(); iterator.hasNext();) {
                SandBot bot = iterator.next();
                if(bot.getFactionID()!=null &&bot.getFactionID().equals(String.valueOf((int)event.getFactionUnClaiming().getId())) && bot.getLocation().getChunk().equals(event.getFLocation().getChunk())){
                    utils.removeBot(bot);
                    ItemStack botItem = new ItemBuilder(Material.valueOf(plugin.getConfig().getString("bot-spawn-item-material").toUpperCase())).setColoredName(plugin.getConfig().getString("bot-spawn-item-name")).setColoredLore(plugin.getConfig().getStringList("bot-spawn-item-lore")).toItemStack();
                    botItem.setAmount(1);
                    player.getInventory().addItem(botItem);
                    Double cash = bot.getBalance();
                    if(cash > 0){
                        LunarSandBot.econ.depositPlayer(player,cash);
                        player.sendMessage(utils.color(plugin.getConfig().getString("cash-back").replace("%amount%",cash.toString())));
                    }
                    counter++;
                }
            }
            player.sendMessage(utils.color(plugin.getConfig().getString("f-unclaim").replace("%amount%",String.valueOf(counter))));
        });

    }

}
