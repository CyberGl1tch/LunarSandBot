package me.magas8.Listeners;

import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import me.magas8.LunarSandBot;
import me.magas8.Managers.ItemBuilder;
import me.magas8.Managers.SandBot;
import me.magas8.Utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class FactionActions implements Listener {
    private LunarSandBot plugin;
    public FactionActions(LunarSandBot plugin){
        this.plugin=plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onFactionDisband(FactionDisbandEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        int counter = 0;
        for(SandBot bot:LunarSandBot.sandBots){
            if(bot.getFactionID().equals(event.getFaction().getId())){
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
    public void onFactionUnclaimEvent(LandUnclaimEvent event){
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        int counter = 0;
        for(SandBot bot:LunarSandBot.sandBots){
            if(bot.getFactionID().equals(event.getFaction().getId())){
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
    public void onFactionUnclaimEvent(LandUnclaimAllEvent event){
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        int counter = 0;
        for(SandBot bot:LunarSandBot.sandBots){
            if(bot.getFactionID().equals(event.getFaction().getId())){
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

}
