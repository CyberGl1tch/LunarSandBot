package me.magas8.Hooks;

import me.magas8.LunarSandBot;

public interface PluginHooks<T> {
    T setup(LunarSandBot plugin);
    String getHookName();
    String getHookPluginName();
}
