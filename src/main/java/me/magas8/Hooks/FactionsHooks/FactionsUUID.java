package me.magas8.Hooks.FactionsHooks;

import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import me.magas8.LunarSandBot;
import me.magas8.Managers.ItemBuilder;
import me.magas8.Managers.SandBot;
import me.magas8.Utils.utils;
import me.magas8.library.FactionsUUID;
import me.magas8.Hooks.FactionHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class FactionsUuid extends FactionHook implements Listener {
    private LunarSandBot plugin;
    public FactionsUuid(LunarSandBot plugin){
        this.plugin=plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
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
    @EventHandler
    public void onFactionDisband(FactionDisbandEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        int counter = 0;
        double cash = 0;
        for(SandBot bot: LunarSandBot.sandBots){
            if(bot.getFactionID()!=null &&bot.getFactionID().equals(event.getFaction().getId())){
                utils.removeBot(bot);
                cash+= bot.getBalance();
                counter++;
            }
        }
        if(counter >0 && player!=null){
            player.sendMessage(utils.color(plugin.getConfig().getString("f-unclaim").replace("%amount%",String.valueOf(counter))));
            ItemStack botItem = new ItemBuilder(Material.valueOf(plugin.getConfig().getString("bot-spawn-item-material").toUpperCase())).setColoredName(plugin.getConfig().getString("bot-spawn-item-name")).setColoredLore(plugin.getConfig().getStringList("bot-spawn-item-lore")).toItemStack();
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
                cash+= bot.getBalance();
                counter++;
            }
        }
        if(counter >0 && player!=null){
            player.sendMessage(utils.color(plugin.getConfig().getString("f-unclaim").replace("%amount%",String.valueOf(counter))));
            ItemStack botItem = new ItemBuilder(Material.valueOf(plugin.getConfig().getString("bot-spawn-item-material").toUpperCase())).setColoredName(plugin.getConfig().getString("bot-spawn-item-name")).setColoredLore(plugin.getConfig().getStringList("bot-spawn-item-lore")).toItemStack();
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
            ItemStack botItem = new ItemBuilder(Material.valueOf(plugin.getConfig().getString("bot-spawn-item-material").toUpperCase())).setColoredName(plugin.getConfig().getString("bot-spawn-item-name")).setColoredLore(plugin.getConfig().getStringList("bot-spawn-item-lore")).toItemStack();
            botItem.setAmount(counter);
            player.getInventory().addItem(botItem);
        }
        if(cash > 0 && player!=null){
            LunarSandBot.econ.depositPlayer(player,cash);
            player.sendMessage(utils.color(plugin.getConfig().getString("cash-back").replace("%amount%",String.valueOf(cash))));
        }
    }
}
