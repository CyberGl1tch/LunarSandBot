package me.magas8.Runnables;

import me.magas8.LunarSandBot;
import me.magas8.Utils.utils;

public class AutoSave implements Runnable {
    private LunarSandBot plugin;
    public AutoSave(LunarSandBot plugin){
        this.plugin=plugin;
    }
    @Override
    public void run() {
        utils.saveDataOfSandbots(plugin.getManager());
        utils.logToServer("SandBots data saved successfully",true);
    }
}
