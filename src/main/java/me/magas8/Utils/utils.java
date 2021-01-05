package me.magas8.Utils;

import com.cryptomorin.xseries.XMaterial;


import me.magas8.Animations.Animation;
import me.magas8.ConfigManager.ConfigFile;
import me.magas8.ConfigManager.FilesManager;
import me.magas8.Enums.GuiTypes;
import me.magas8.GUIS.FactionManageGui;
import me.magas8.GUIS.MenuManager;
import me.magas8.GUIS.RemoveGUI;
import me.magas8.GUIS.SandBotGui;
import me.magas8.Hooks.FactionHook;
import me.magas8.LunarSandBot;
import me.magas8.Managers.ItemBuilder;
import me.magas8.Managers.SandBot;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class utils {
    private static LunarSandBot plugin = LunarSandBot.getPlugin(LunarSandBot.class);
   private static FactionHook factionHook = (FactionHook)plugin.getHookManager().getPluginMap().get("Factions");
    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    public static Color enabledArmorColor = Color.fromRGB(plugin.getConfig().getInt("bot-enabled-red"), plugin.getConfig().getInt("bot-enabled-green"), plugin.getConfig().getInt("bot-enabled-blue"));
    public static Color disabledArmorColor = Color.fromRGB(plugin.getConfig().getInt("bot-disabled-red"), plugin.getConfig().getInt("bot-disabled-green"), plugin.getConfig().getInt("bot-disabled-blue"));


    public static void dropitem(Player player, Location loc, ItemStack item){
        ItemStack tmpitem = item;
        Entity drop = player.getWorld().dropItem(loc,item);
        Vector v = new Vector().setY(0.7);
        drop.setVelocity(v);
    }


    public static  void spawnBot(Player player,Location loc){

        if(factionHook.getFactionId(player) == null){
            player.sendMessage(utils.color(plugin.getConfig().getString("no-faction")));
            return;
        }
        if(factionHook.getFactionIdAtLocation(loc) == null || !factionHook.getFactionIdAtLocation(loc).equals(factionHook.getFactionId(player))){
            player.sendMessage(utils.color(plugin.getConfig().getString("no-claim")));
            return;
        }
        if(getFactionAliveBotsCount(factionHook.getFactionId(player))>= plugin.getConfig().getInt("faction-bot-limit")){
            player.sendMessage(utils.color(plugin.getConfig().getString("faction-bot-full")));
            return;
        }
        for(SandBot bot: LunarSandBot.sandBots){
            if(bot.getLocation() == null || bot.getLocation().getWorld() == null) continue;
            if(bot.getLocation().getWorld().equals(loc.getWorld())) {
                if (bot.getLocation().distance(loc) <= 2*plugin.getConfig().getInt("bot-fill-radius") && factionHook.getFactionIdAtLocation(loc)!=null && factionHook.getFactionIdAtLocation(bot.getLocation())!=null &&factionHook.getFactionIdAtLocation(loc).equals(factionHook.getFactionIdAtLocation(bot.getLocation()))) {
                    player.sendMessage(utils.color(plugin.getConfig().getString("bot-close-to-each-other").replace("%distance%", String.valueOf(2*plugin.getConfig().getInt("bot-fill-radius") - (int) bot.getLocation().distance(loc)))));
                    return;
                }
            }
        }
        ItemStack item = player.getItemInHand();
        if(item.getAmount() ==1){
            player.getInventory().remove(item);
        }else{
            player.getItemInHand().setAmount(item.getAmount()-1);
        }

        ArmorStand stand = loc.getWorld().spawn(loc,ArmorStand.class);
        stand.setVisible(true);
        stand.setArms(true);
        stand.setGravity(false);
        stand.setSmall(false);
        stand.setBasePlate(false);

        stand.setItemInHand(new ItemStack(Material.SAND));

        ItemStack playerhead = new ItemStack(Material.SKULL_ITEM,1,(byte) 3);
        SkullMeta meta = (SkullMeta) playerhead.getItemMeta();
        meta.setOwner(player.getName());
        meta.setDisplayName(player.getName());
        playerhead.setItemMeta(meta);
        stand.setCustomName("ignoreArmorstand");

        stand.setHelmet(playerhead);
        stand.setChestplate(utils.getColorArmor(Material.LEATHER_CHESTPLATE, disabledArmorColor));
        stand.setLeggings(utils.getColorArmor(Material.LEATHER_LEGGINGS, disabledArmorColor));
        stand.setBoots(utils.getColorArmor(Material.LEATHER_BOOTS, disabledArmorColor));

        EulerAngle rarm = stand.getRightArmPose();
        EulerAngle larm = stand.getLeftArmPose();
        EulerAngle newrarm = rarm.setX(-1.5f).setZ(-0.3f).setY(0.5f);
        EulerAngle newlarm =larm.setX(-0.2f).setZ(-0.2f);
        stand.setRightArmPose(newrarm);
        stand.setLeftArmPose(newlarm);

        EulerAngle rleg = stand.getRightLegPose();
        EulerAngle lleg = stand.getLeftLegPose();
        EulerAngle newrleg = rleg.setX(-0.2f).setZ(0.2f);
        EulerAngle newlleg = lleg.setX(0.2f).setZ(-0.2f);
        stand.setRightLegPose(newrleg);
        stand.setLeftLegPose(newlleg);
        stand.setCustomNameVisible(true);
        stand.setCustomName(utils.color(plugin.getConfig().getString("bot-custom-name").replace("%faction%",factionHook.getFactionTag(player))));


        SandBot bot = new SandBot(stand,player);
        LunarSandBot.sandBots.add(bot);

        ConcurrentHashMap<GuiTypes, MenuManager> menus = new ConcurrentHashMap<>();
        menus.put(GuiTypes.REMOVEGUI,new RemoveGUI(plugin,bot));
        menus.put(GuiTypes.FACTIONGUI,new FactionManageGui(plugin));
        menus.put(GuiTypes.SANDBOTGUI,new SandBotGui(plugin,bot));
        LunarSandBot.botGuis.put(bot,menus);
        new Animation(bot,plugin).runTaskTimerAsynchronously(plugin,0,2);
    }


    public static ItemStack getColorArmor(Material m, Color c) {
        ItemStack i = new ItemStack(m, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) i.getItemMeta();
        meta.setColor(c);
        meta.setDisplayName("SandBotArmor");
        i.setItemMeta(meta);
        return i;
    }

    public static String getFactionID(Player player){
        return factionHook.getFactionId(player);
    }


    public static Integer getFactionAliveBotsCount(String factionID){
        Integer count = 0;
        for(SandBot bot: LunarSandBot.sandBots){
            if(bot.getFactionID().equals(factionID)){
                count++;
            }
        }
        return count;
    }


    public static void setupConfigFiles(FilesManager manager){
        manager.folderSetup("Data");
        manager.fileSetup("config");
        manager.fileSetup("sandbots","Data");
    }


    public static void saveDataOfSandbots(FilesManager manager){
        if(manager.getAllFiles("default/Data").values().size()<1) return;
        FileConfiguration data = manager.getFile("default/Data/sandbots.yml").getConfig();
        for(SandBot bot: LunarSandBot.sandBots){
            data.set("SandBots."+bot.getSandbotUUID()+".sandBotUuid",bot.getSandbotUUID().toString());
            data.set("SandBots."+bot.getSandbotUUID()+".ownerUuid",bot.getOwnerUUID().toString());
            data.set("SandBots."+bot.getSandbotUUID()+".ownerName",bot.getOwnerName());
            data.set("SandBots."+bot.getSandbotUUID()+".location",locationToString(bot.getLocation()));
            data.set("SandBots."+bot.getSandbotUUID()+".balance",bot.getBalance());
            data.set("SandBots."+bot.getSandbotUUID()+".isActivated",bot.isActivated());
            data.set("SandBots."+bot.getSandbotUUID()+".factionID",bot.getFactionID());
            data.set("SandBots."+bot.getSandbotUUID()+".animate",bot.isAnimate());

            ArrayList<String> blockLocations = new ArrayList<>();
            for(Block block :bot.getLapisBlocks()){
                blockLocations.add(locationToString(block.getLocation()));
            }
            data.set("SandBots."+bot.getSandbotUUID()+".lapisBlocks",blockLocations);
        }
        manager.getFile("default/Data/sandbots.yml").saveConfig();
    }
    public static void removeBotFromConfigIfExist(FilesManager manager,SandBot bot){
        ConfigFile data = manager.getFile("default/Data/sandbots.yml");
        if(data.getConfig().isSet("SandBots."+bot.getSandbotUUID())){
            data.getConfig().set("SandBots."+bot.getSandbotUUID(),null);
            data.saveConfig();
        }
    }


    public static void loadDataOfSandbots(FilesManager manager){
        Integer counter = 0;
        ConfigFile data = manager.getFile("default/Data/sandbots.yml");
        if(data.getConfig().isConfigurationSection("SandBots")) {
            for (String sandBotsUUIDS : data.getConfig().getConfigurationSection("SandBots").getKeys(false)) {
                ArrayList<Block> lapisBlocks = new ArrayList<>();
                for (String locstring : data.getConfig().getStringList("SandBots." + sandBotsUUIDS + ".lapisBlocks")) {
                    lapisBlocks.add(stringToLocation(locstring).getBlock());
                }
                SandBot bot = new SandBot(UUID.fromString(data.getConfig().getString("SandBots." + sandBotsUUIDS + ".sandBotUuid")),
                        UUID.fromString(data.getConfig().getString("SandBots." + sandBotsUUIDS + ".ownerUuid")),
                        data.getConfig().getString("SandBots." + sandBotsUUIDS + ".ownerName"),
                        lapisBlocks, data.getConfig().getDouble("SandBots." + sandBotsUUIDS + ".balance"),
                        stringToLocation(data.getConfig().getString("SandBots." + sandBotsUUIDS + ".location")),
                        data.getConfig().getBoolean("SandBots." + sandBotsUUIDS + ".isActivated"),
                        data.getConfig().getString("SandBots." + sandBotsUUIDS + ".factionID"),
                        data.getConfig().getBoolean("SandBots." + sandBotsUUIDS + ".animate")
                );

                LunarSandBot.sandBots.add(bot);
                if(bot.getLocation() !=null && bot.getLocation().getWorld() !=null) bot.getLocation().getWorld().loadChunk(bot.getLocation().getChunk());
                ConcurrentHashMap<GuiTypes, MenuManager> menus = new ConcurrentHashMap<>();
                menus.put(GuiTypes.REMOVEGUI,new RemoveGUI(plugin,bot));
                menus.put(GuiTypes.FACTIONGUI,new FactionManageGui(plugin));
                menus.put(GuiTypes.SANDBOTGUI,new SandBotGui(plugin,bot));

                LunarSandBot.botGuis.put(bot,menus);
                counter++;
                new Animation(bot,plugin).runTaskTimerAsynchronously(plugin,0,2);
            }
        }
        Bukkit.getConsoleSender().sendMessage(color("&8&l[&a&l!&8&l] Successfully loaded "+counter+" SandBots"));
    }


    public static String locationToString(Location location){
        if(location == null || location.getWorld() == null) return "Unknown:0:0:0";
        String loc = location.getX()+":"+location.getY()+":"+location.getZ()+":"+location.getWorld().getName();
        return loc;
    }


    public static Location stringToLocation(String string){
        Location loc;
        try {
             loc = new Location(Bukkit.getWorld(string.split(":")[3]), Double.parseDouble(string.split(":")[0]), Double.parseDouble(string.split(":")[1]), Double.parseDouble(string.split(":")[2]));
        }catch (Exception e){
            return null;
        }
        return loc;
    }

    public static Entity getEntityByUUID(UUID uuid){
        for(World world :Bukkit.getServer().getWorlds()){
            for(Entity entity : world.getEntities()){
                if(entity.getUniqueId().equals(uuid)){
                    return entity;
                }
            }
        }
        return null;
    }


    public static ArrayList<Block> getNearbyBlocks(SandBot s){
        ArrayList<Block> blocks = new ArrayList<>();
        Integer offsetX = plugin.getConfig().getInt("bot-fill-radius");
        Integer offsetY = plugin.getConfig().getInt("bot-fill-radius");
        Integer offsetZ = plugin.getConfig().getInt("bot-fill-radius");
        for (int x = s.getLocation().getBlockX() - offsetX; x <= s.getLocation().getBlockX() + offsetX; x++) {
            for (int y = s.getLocation().getBlockY() - offsetY; y <= s.getLocation().getBlockY() + offsetY; y++) {
                for (int z = s.getLocation().getBlockZ() - offsetZ; z <= s.getLocation().getBlockZ() + offsetZ; z++) {
                    Block block = s.getSandbotModel().getWorld().getBlockAt(x, y, z);
                    if (block.getType().equals(XMaterial.valueOf(plugin.getConfig().getString("target-block").toUpperCase()).parseMaterial())) {
                        if(factionHook.getFactionIdAtLocation(block.getLocation()) !=null && factionHook.getFactionIdAtLocation(block.getLocation()).equals(s.getFactionID())) {
                            blocks.add(s.getSandbotModel().getWorld().getBlockAt(x, y, z));
                        }
                    }
                }
            }
        }
        return blocks;
    }



    public static ItemStack botItem(String name, List<String> lore, SandBot bot){
        ArrayList<String> updatedPlaceholdersLore = new ArrayList<>();
        for(String s : lore){
            updatedPlaceholdersLore.add(s.replace("%botowner%",bot.getOwnerName())
            .replace("%botfaction%",factionHook.getFactionTag(Bukkit.getServer().getOfflinePlayer(bot.getOwnerUUID())))
            .replace("%balance%",bot.getBalance().toString())
            .replace("%isActivated%", bot.isActivated() ? plugin.getConfig().getString("bot-core-enabled-placeholder") : plugin.getConfig().getString("bot-core-disabled-placeholder"))
            .replace("%blocksd%",String.valueOf(bot.getLapisBlocks().size()))
            .replace("%botlocationx%",String.valueOf(bot.getLocation().getX()))
            .replace("%botlocationy%",String.valueOf(bot.getLocation().getY()))
            .replace("%botlocationz%",String.valueOf(bot.getLocation().getZ())));
        }
        return new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem()).setSkullOwner(bot.getOwnerName()).setColoredName(name).setColoredLore(updatedPlaceholdersLore).toItemStack();
    }


    public static ItemStack botItem(String name, List<String> lore, SandBot bot,ItemStack item){
        ArrayList<String> updatedPlaceholdersLore = new ArrayList<>();
        for(String s : lore){
            updatedPlaceholdersLore.add(s.replace("%botowner%",bot.getOwnerName())
                    .replace("%botfaction%",factionHook.getFactionTag(Bukkit.getServer().getOfflinePlayer(bot.getOwnerUUID())))
                    .replace("%balance%",bot.getBalance().toString())
                    .replace("%isActivated%",bot.isActivated() ? plugin.getConfig().getString("bot-core-enabled-placeholder") : plugin.getConfig().getString("bot-core-disabled-placeholder"))
                    .replace("%blocksd%",String.valueOf(bot.getLapisBlocks().size()))
                    .replace("%botlocationx%",String.valueOf(bot.getLocation().getX()))
                    .replace("%botlocationy%",String.valueOf(bot.getLocation().getY()))
                    .replace("%botlocationz%",String.valueOf(bot.getLocation().getZ())));
        }
        return new ItemBuilder(item).setColoredName(name).setColoredLore(updatedPlaceholdersLore).toItemStack();
    }
    public static void removeBot(SandBot bot) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getUniqueId().equals(bot.getSandbotUUID()))
                    entity.remove();
            }
        }

        utils.removeBotFromConfigIfExist(plugin.getManager(),bot);
        LunarSandBot.botGuis.remove(bot);
        LunarSandBot.sandBots.remove(bot);
    }
    public static boolean getArmorAnimateBoolean(){
        return plugin.getConfig().getBoolean("bot-animate-armor");
    }
    public static void logToServer(String text,Boolean prefix){
        Bukkit.getServer().getConsoleSender().sendMessage((prefix ? "[LunarSandBot] " : "") + text);
    }

}
