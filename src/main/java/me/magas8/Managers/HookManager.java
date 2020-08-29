package me.magas8.Managers;

import me.magas8.Hooks.FactionHook;
import me.magas8.Hooks.PluginHooks;
import me.magas8.LunarSandBot;

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
        if(this.plugin.getServer().getPluginManager().getPlugin(hook.getHookName()) == null){
            this.plugin.getServer().getLogger().log(Level.WARNING,"[SandBot] Cant find any "+hook.getHookName()+" hook!");
            return;
        }
        this.hooksMap.put(hook.getHookName(), (PluginHooks) hook.setup());
    }
    public HashMap<String, PluginHooks> getPluginMap() {
        return this.hooksMap;
    }
}
