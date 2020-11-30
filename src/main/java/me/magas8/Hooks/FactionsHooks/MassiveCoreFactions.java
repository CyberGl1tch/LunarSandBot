package me.magas8.Hooks.FactionsHooks;




import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.event.*;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
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
        Faction locfaction = BoardColl.get().getFactionAt(PS.valueOf(loc));
        if(locfaction==null) return null;
        if(!locfaction.isNormal()) return null;
        return locfaction.getId();
    }
    @EventHandler
    public void onFactionDisband(EventFactionsDisband event) {
        if (event.isCancelled()) return;
        MPlayer player = event.getMPlayer();
        int counter = 0;
        double cash = 0;
        for(SandBot bot: LunarSandBot.sandBots){
            if(bot.getFactionID()!=null &&bot.getFactionID().equals(event.getFaction().getId())){
                utils.removeBot(bot);
                cash += bot.getBalance();
                counter++;
            }
        }
        if(counter >0 && player!=null){
            player.getPlayer().sendMessage(utils.color(plugin.getConfig().getString("f-unclaim").replace("%amount%",String.valueOf(counter))));
            ItemStack botItem = new ItemBuilder(XMaterial.matchXMaterial(plugin.getConfig().getString("bot-spawn-item-material")).get().parseItem()).setColoredName(plugin.getConfig().getString("bot-spawn-item-name")).setColoredLore(plugin.getConfig().getStringList("bot-spawn-item-lore")).toItemStack();
            botItem.setAmount(counter);
            player.getPlayer().getInventory().addItem(botItem);
        }
        if(cash > 0 && player!=null){
            LunarSandBot.econ.depositPlayer(player.getPlayer(),cash);
            player.getPlayer().getPlayer().sendMessage(utils.color(plugin.getConfig().getString("cash-back").replace("%amount%",String.valueOf(cash))));
        }

    }

    @EventHandler
    public void onFactionUnclaimEvent(EventFactionsChunksChange event){
        if (event.isCancelled()) return;
        MPlayer player = event.getMPlayer();
        int counter = 0;
        double cash = 0;

        for(SandBot bot:LunarSandBot.sandBots){
            if(bot.getFactionID()!=null && bot.getFactionID().equals(event.getMPlayer().getFaction().getId()) && event.getChunkType().get(PS.valueOf(bot.getLocation().getChunk()))!=null && ( event.getChunkType().get(PS.valueOf(bot.getLocation().getChunk())).equals(EventFactionsChunkChangeType.SELL) || event.getChunkType().get(PS.valueOf(bot.getLocation().getChunk())).equals(EventFactionsChunkChangeType.CONQUER)) && event.getChunks().contains(PS.valueOf(bot.getLocation().getChunk()))){
                utils.removeBot(bot);
                cash+=bot.getBalance();
                counter++;
            }
        }
        if(counter >0 && player!=null){
            player.getPlayer().sendMessage(utils.color(plugin.getConfig().getString("f-unclaim").replace("%amount%",String.valueOf(counter))));
            ItemStack botItem = new ItemBuilder(XMaterial.matchXMaterial(plugin.getConfig().getString("bot-spawn-item-material")).get().parseItem()).setColoredName(plugin.getConfig().getString("bot-spawn-item-name")).setColoredLore(plugin.getConfig().getStringList("bot-spawn-item-lore")).toItemStack();
            botItem.setAmount(counter);
            player.getPlayer().getInventory().addItem(botItem);
        }
        if(cash > 0 && player!=null){
            LunarSandBot.econ.depositPlayer(player.getPlayer(),cash);
            player.getPlayer().sendMessage(utils.color(plugin.getConfig().getString("cash-back").replace("%amount%",String.valueOf(cash))));
        }
    }

}
