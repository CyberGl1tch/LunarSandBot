package me.magas8.Hooks.FactionsHooks;




import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.entity.MPlayer;

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

public class MassiveCoreFactions extends FactionHook implements Listener {
    private LunarSandBot plugin;
    public MassiveCoreFactions(LunarSandBot plugin){
        this.plugin=plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @Override
    public Boolean isFactionAdmin(Player player) {
        if(MPlayer.get(player).getRole().equals(Rel.OFFICER) || MPlayer.get(player).getRole().equals(Rel.LEADER)){
            return true;
        }
        return false;
    }
    @Override
    public String getFactionTag(Player player) {
        MPlayer fPlayer = MPlayer.get(player);
        if (fPlayer.getFaction() == null)
            return null;
        return fPlayer.getFaction().getName();
    }
    @Override
    public String getFactionTag(OfflinePlayer player) {
        MPlayer fPlayer = MPlayer.get(player);
        if (fPlayer.getFaction() == null)
            return null;
        return fPlayer.getFaction().getName();
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
        MPlayer fPlayer = MPlayer.get(player);
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
        for(SandBot bot: LunarSandBot.sandBots){
            if(bot.getFactionID()!=null &&bot.getFactionID().equals(event.getFaction().getId())){
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
            if(bot.getFactionID()!=null && bot.getFactionID().equals(event.getFaction().getId()) && bot.getLocation().getChunk().equals(event.getLocation().getChunk())){
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
            if(bot.getFactionID()!=null &&bot.getFactionID().equals(event.getFaction().getId())){
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
