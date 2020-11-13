package me.magas8.Listeners;

import me.magas8.Hooks.FactionHook;
import me.magas8.LunarSandBot;
import me.magas8.Managers.ItemBuilder;
import me.magas8.Managers.SandBot;
import me.magas8.Utils.utils;
import net.prosavage.factionsx.event.FactionDisbandEvent;
import net.prosavage.factionsx.event.FactionUnClaimAllEvent;
import net.prosavage.factionsx.event.FactionUnClaimEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class FactionsXListeners implements Listener {
    private LunarSandBot plugin;
    public FactionsXListeners(LunarSandBot plugin){
        this.plugin=plugin;
        if(!FactionHook.hookName.equalsIgnoreCase("FactionsX")) return;
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
