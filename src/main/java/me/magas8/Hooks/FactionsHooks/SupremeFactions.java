package me.magas8.Hooks.FactionsHooks;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.*;


import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.struct.Role;
import me.magas8.Hooks.FactionHook;
import me.magas8.LunarSandBot;
import me.magas8.Managers.ItemBuilder;
import me.magas8.Managers.SandBot;
import me.magas8.Utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class SupremeFactions extends FactionHook implements Listener {
    private LunarSandBot plugin;
    public SupremeFactions(LunarSandBot plugin){
        this.plugin=plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public Boolean isFactionAdmin(Player player) {
        if(FPlayers.getInstance().getByPlayer(player).getRole().equals(Role.valueOf("COLEADER")) || FPlayers.getInstance().getByPlayer(player).getRole().equals(Role.valueOf("MODERATOR")) || FPlayers.getInstance().getByPlayer(player).getRole().equals(Role.valueOf("ADMIN"))){
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
    @EventHandler
    public void onFactionDisband(FactionDisbandEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        int counter = 0;
        double cash= 0;
        for(SandBot bot: LunarSandBot.sandBots){
            if(bot.getFactionID()!=null &&bot.getFactionID().equals(event.getFaction().getId())){
                utils.removeBot(bot);
                cash += bot.getBalance();
                counter++;
            }
        }
        if(counter >0 && player!=null){
            player.sendMessage(utils.color(plugin.getConfig().getString("f-unclaim").replace("%amount%",String.valueOf(counter))));
            ItemStack botItem = new ItemBuilder(XMaterial.matchXMaterial(plugin.getConfig().getString("bot-spawn-item-material")).get().parseItem()).setColoredName(plugin.getConfig().getString("bot-spawn-item-name")).setColoredLore(plugin.getConfig().getStringList("bot-spawn-item-lore")).toItemStack();
            botItem.setAmount(counter);
            player.getInventory().addItem(botItem);
        }
        if(cash > 0 && player!=null){
            LunarSandBot.econ.depositPlayer(player,cash);
            player.sendMessage(utils.color(plugin.getConfig().getString("cash-back").replace("%amount%",String.valueOf(cash))));
        }

    }

    @EventHandler
    public void onFactionUnclaimEvent(LandUnclaimEvent event){
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        int counter = 0;
        double cash = 0;

        for(SandBot bot:LunarSandBot.sandBots){
            if(bot.getFactionID()!=null && bot.getFactionID().equals(event.getFaction().getId()) && bot.getLocation().getChunk().equals(event.getLocation().getChunk())){
                utils.removeBot(bot);
                cash+=bot.getBalance();
                counter++;
            }
        }
        if(counter >0 && player!=null){
            player.sendMessage(utils.color(plugin.getConfig().getString("f-unclaim").replace("%amount%",String.valueOf(counter))));
            ItemStack botItem = new ItemBuilder(XMaterial.matchXMaterial(plugin.getConfig().getString("bot-spawn-item-material")).get().parseItem()).setColoredName(plugin.getConfig().getString("bot-spawn-item-name")).setColoredLore(plugin.getConfig().getStringList("bot-spawn-item-lore")).toItemStack();
            botItem.setAmount(counter);
            player.getInventory().addItem(botItem);
        }
        if(cash > 0 && player!=null){
            LunarSandBot.econ.depositPlayer(player,cash);
            player.sendMessage(utils.color(plugin.getConfig().getString("cash-back").replace("%amount%",String.valueOf(cash))));
        }
    }
    @EventHandler
    public void onFactionUnclaimEvent(LandUnclaimAllEvent event){
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        int counter = 0;
        double cash = 0;
        for(SandBot bot:LunarSandBot.sandBots){
            if(bot.getFactionID()!=null &&bot.getFactionID().equals(event.getFaction().getId())){
                utils.removeBot(bot);
                cash+= bot.getBalance();
                counter++;
            }
        }
        if(counter >0 && player!=null){
            player.sendMessage(utils.color(plugin.getConfig().getString("f-unclaim").replace("%amount%",String.valueOf(counter))));
            ItemStack botItem = new ItemBuilder(XMaterial.matchXMaterial(plugin.getConfig().getString("bot-spawn-item-material")).get().parseItem()).setColoredName(plugin.getConfig().getString("bot-spawn-item-name")).setColoredLore(plugin.getConfig().getStringList("bot-spawn-item-lore")).toItemStack();
            botItem.setAmount(counter);
            player.getInventory().addItem(botItem);
        }
        if(cash > 0 && player!=null){
            LunarSandBot.econ.depositPlayer(player,cash);
            player.sendMessage(utils.color(plugin.getConfig().getString("cash-back").replace("%amount%",String.valueOf(cash))));
        }
    }
}
