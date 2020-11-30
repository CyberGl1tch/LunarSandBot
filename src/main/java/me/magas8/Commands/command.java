package me.magas8.Commands;


import com.cryptomorin.xseries.XMaterial;
import me.magas8.LunarSandBot;
import me.magas8.Managers.ItemBuilder;
import me.magas8.Runnables.BlockSpawn;
import me.magas8.Utils.Updater;
import me.magas8.Utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class command implements CommandExecutor {
    private String sandbot = "sandbot";
    private LunarSandBot plugin;




    public command(LunarSandBot plugin) {
        this.plugin = plugin;
        plugin.getCommand(sandbot).setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

            if(cmd.getName().equalsIgnoreCase(sandbot)){
                    if(args.length<1){
                        sender.sendMessage(utils.color(plugin.getConfig().getString("command-usage")));
                        return true;
                    }
                    if(args[0].equalsIgnoreCase("reload")){
                        if(sender.hasPermission("sandbot.reload")){
                            plugin.reloadConfig();
                            utils.saveDataOfSandbots(plugin.getManager());
                            utils.setupConfigFiles(plugin.getManager());
                            LunarSandBot.sandBots.clear();
                            LunarSandBot.botGuis.clear();
                            utils.loadDataOfSandbots(plugin.getManager());
                            if(LunarSandBot.spawnSandBlocks!=null) LunarSandBot.spawnSandBlocks.cancel();
                            LunarSandBot.spawnSandBlocks = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> Bukkit.getScheduler().runTask(plugin, new BlockSpawn(plugin)), 0, plugin.getConfig().getInt("sand-spawn-speed"));
                            sender.sendMessage(utils.color(plugin.getConfig().getString("command-reload")));
                            return true;
                        }else{
                            sender.sendMessage(utils.color(plugin.getConfig().getString("no-perm")));
                        }
                    }
                    if(args[0].equalsIgnoreCase("give")){
                        if(sender.hasPermission("sandbot.give")) {
                            if (args.length < 2) {
                                sender.sendMessage(utils.color(plugin.getConfig().getString("command-usage")));
                                return true;
                            }
                            Player p = Bukkit.getPlayer(args[1]);
                            if (p == null) {
                                sender.sendMessage(utils.color(plugin.getConfig().getString("command-player-error")));
                                return true;
                            }
                            if (args.length < 3) {
                                sender.sendMessage(utils.color(plugin.getConfig().getString("command-usage")));
                                return true;
                            }
                            try {
                                Integer howmany = Integer.parseInt(args[2]);
                                if (XMaterial.matchXMaterial(plugin.getConfig().getString("bot-spawn-item-material")).isPresent()) {
                                    ItemStack bot = new ItemBuilder(XMaterial.matchXMaterial(plugin.getConfig().getString("bot-spawn-item-material")).get().parseItem()).setColoredName(plugin.getConfig().getString("bot-spawn-item-name")).setColoredLore(plugin.getConfig().getStringList("bot-spawn-item-lore")).toItemStack();

                                    bot.setAmount(howmany);
                                    p.getInventory().addItem(bot);
                                    p.sendMessage(utils.color(plugin.getConfig().getString("command-succeed").replace("%player%", args[1]).replace("%amount%", howmany.toString())));
                                }else{
                                    p.sendMessage(utils.color("&c&lUnknown item "+plugin.getConfig().getString("bot-spawn-item-material")));
                                }
                            } catch (NumberFormatException e) {
                                sender.sendMessage(utils.color(plugin.getConfig().getString("command-amount-error")));

                            }
                        }else{
                            sender.sendMessage(utils.color(plugin.getConfig().getString("no-perm")));
                        }

                    }else if(args[0].equalsIgnoreCase("update")){
                        if(sender.hasPermission("lunarsandbot.update")) {
                            Updater updater = new Updater(plugin, LunarSandBot.pluginID, plugin.getPluginFile(), Updater.UpdateType.DOWNLOAD, false);
                            switch (updater.getResult()) {
                                case FAILED:
                                    sender.sendMessage(utils.color("&8&l[&4&l!&8&l] &cOh.. Something gone wrong download LunarSandBot from the official spigot page!"));
                                    break;
                                case SUCCESS:
                                    sender.sendMessage(utils.color("&8&l[&2&l!&8&l] &aUpdate Completed!"));
                                    break;
                            }
                        }else{
                            sender.sendMessage(utils.color(plugin.getConfig().getString("no-perm")));
                        }

                    }else {
                        sender.sendMessage(utils.color(plugin.getConfig().getString("command-usage")));
                        return  true;
                    }




            }


        return true;
    }
}