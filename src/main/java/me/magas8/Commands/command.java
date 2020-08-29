package me.magas8.Commands;


import me.magas8.LunarSandBot;
import me.magas8.Managers.ItemBuilder;
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
                            LunarSandBot.sandBots.clear();
                            LunarSandBot.botGuis.clear();
                            utils.setupConfigFiles(plugin.getManager());
                            utils.loadDataOfSandbots(plugin.getManager());
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
                                ItemStack bot = new ItemBuilder(Material.valueOf(plugin.getConfig().getString("bot-spawn-item-material").toUpperCase())).setColoredName(plugin.getConfig().getString("bot-spawn-item-name")).setColoredLore(plugin.getConfig().getStringList("bot-spawn-item-lore")).toItemStack();
                                bot.setAmount(howmany);
                                p.getInventory().addItem(bot);
                                p.sendMessage(utils.color(plugin.getConfig().getString("command-succeed").replace("%player%", args[1]).replace("%amount%", howmany.toString())));
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