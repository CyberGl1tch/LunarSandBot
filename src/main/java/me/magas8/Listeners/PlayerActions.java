package me.magas8.Listeners;

import com.cryptomorin.xseries.XMaterial;
import me.magas8.Enums.GuiTypes;
import me.magas8.GUIS.FactionManageGui;
import me.magas8.GUIS.SandBotGui;
import me.magas8.Hooks.FactionHook;
import me.magas8.LunarSandBot;
import me.magas8.Managers.SandBot;
import me.magas8.Utils.Updater;
import me.magas8.Utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerActions implements Listener {
    private LunarSandBot plugin;
    private FactionHook factionHook;
    public PlayerActions(LunarSandBot plugin){
        this.plugin=plugin;
        this.factionHook= (FactionHook)this.plugin.getHookManager().getPluginMap().get("Factions");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        if(player.hasPermission("lunarsandbot.updates") || player.isOp()){
            if(LunarSandBot.updateResult!=null && LunarSandBot.updateResult.equals(Updater.Result.UPDATE_FOUND)){
                plugin.getServer().getScheduler().runTaskLater(plugin,()->{
                    player.sendMessage(utils.color("&8&l[&c&l!&8&l] &7LunarSandBot is outdated current version is &c"+plugin.getDescription().getVersion()+"&7 Newest version is &a"+LunarSandBot.version));
                    player.sendMessage(utils.color("&8&l[&c&l!&8&l] &7Download latest update at\n"+"&ahttps://www.spigotmc.org/resources/82608&7\n"+"&7or use /sandbot update"));
                },20);
            }
        }
    }

    @EventHandler
    public void onPlaceSandBot(PlayerInteractEvent e){
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInHand();
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && item.getType().equals(Material.valueOf(plugin.getConfig().getString("bot-spawn-item-material").toUpperCase())) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(utils.color(plugin.getConfig().getString("bot-spawn-item-name")))){
           e.setCancelled(true);
            Block blockclick = e.getClickedBlock();
            Location loc = blockclick.getLocation();
            loc.setY(loc.getY() + 1);
            loc.setZ(loc.getZ() + 0.5);
            loc.setX(loc.getX() + 0.5);
            utils.spawnBot(player,loc);
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == XMaterial.valueOf(plugin.getConfig().getString("target-block").toUpperCase()).parseMaterial())
            for (SandBot bot : LunarSandBot.sandBots) {
                if (bot.getLocation().toVector().distance(event.getBlock().getLocation().toVector()) <= plugin.getConfig().getInt("bot-fill-radius")) {
                    if(factionHook.getFactionIdAtLocation(event.getBlock().getLocation()) != null && factionHook.getFactionIdAtLocation(event.getBlock().getLocation()).equals(bot.getFactionID())) {
                        bot.addBlock(event.getBlock());
                        return;
                    }
                }
            }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == XMaterial.valueOf(plugin.getConfig().getString("target-block").toUpperCase()).parseMaterial())
            for (SandBot bot : LunarSandBot.sandBots) {
                if (bot.getLocation().toVector().distance(event.getBlock().getLocation().toVector()) <= plugin.getConfig().getInt("bot-fill-radius")) {
                    if(factionHook.getFactionIdAtLocation(event.getBlock().getLocation())!=null &&factionHook.getFactionIdAtLocation(event.getBlock().getLocation()).equals(bot.getFactionID())) {
                        bot.removeBlock(event.getBlock());
                        return;
                    }
                }
            }
    }
    @EventHandler
    public void onDamageEvent(EntityDamageEvent e) {
        Entity armorstand = e.getEntity();
        SandBot bot = null;
        if (armorstand instanceof ArmorStand) {
            for(SandBot bottmp : LunarSandBot.sandBots){
                if(bottmp.getSandbotUUID().equals(armorstand.getUniqueId())){
                    bot=bottmp;
                    break;
                }
            }
           if(bot!=null)
                e.setCancelled(true);
            }
        }


    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity armorstand = event.getRightClicked();
        SandBot bot = null;
        if (armorstand.getType().equals(EntityType.ARMOR_STAND)) {
                for(SandBot bottmp : LunarSandBot.sandBots){
                    if(bottmp.getSandbotUUID().equals(armorstand.getUniqueId())){
                        bot=bottmp;
                        break;
                    }
                }
            if(bot !=null) {
                event.setCancelled(true);
                if (!bot.getFactionID().equals(factionHook.getFactionId(player))) {
                    player.sendMessage(utils.color(plugin.getConfig().getString("not-your-faction-bots")));
                    return;
                }
                ((SandBotGui)LunarSandBot.botGuis.get(bot).get(GuiTypes.SANDBOTGUI)).open(player);
            }

        }
    }
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        String[] kati = e.getMessage().split(" ");

        if (kati.length < 1) {
            return;
        }
        if (kati[0].equalsIgnoreCase("/f")) {
            if(e.getPlayer() != null) {
                Player player = e.getPlayer();
                if(kati.length <2){
                    return;
                }
                if(kati[1].equalsIgnoreCase("sandbots") || kati[1].equalsIgnoreCase("sandbot")){
                    if(factionHook.getFactionId(player) !=null) {
                        new FactionManageGui(plugin).open(player);
                    }else{
                        e.getPlayer().sendMessage(utils.color(plugin.getConfig().getString("no-faction")));
                    }
                    e.setCancelled(true);
                }
            }

        }
    }
}
