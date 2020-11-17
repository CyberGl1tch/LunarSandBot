package me.magas8.Managers;

import me.magas8.Hooks.FactionHook;
import me.magas8.Hooks.PluginHooks;
import me.magas8.LunarSandBot;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.logging.Level;

public class HookManager {
    private LunarSandBot plugin;
    private HashMap<String, PluginHooks> hooksMap = new HashMap<>();
    public HookManager(LunarSandBot plugin){
        this.plugin=plugin;
        hook(new FactionHook());
    }
    private void hook(PluginHooks hook){
        PluginHooks hooked = (PluginHooks) hook.setup(plugin);
        if(this.plugin.getServer().getPluginManager().getPlugin(hook.getHookPluginName()) == null){
            this.plugin.getServer().getLogger().log(Level.WARNING,"[SandBot] Cant find any "+hook.getHookName()+" hook!");
            Bukkit.getScheduler().runTask(plugin,()->{
                this.plugin.getServer().getPluginManager().disablePlugin(plugin);
            });
            return;
        }
        this.hooksMap.put("Factions", hooked);
        Bukkit.getConsoleSender().sendMessage("Successfully hooked "+hook.getHookName());
    }
    public HashMap<String, PluginHooks> getPluginMap() {
        return this.hooksMap;
    }
}
