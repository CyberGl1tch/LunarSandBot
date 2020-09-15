package me.magas8.Hooks;

public interface PluginHooks<T> {
    T setup();
    String getHookName();
    String getHookPluginName();
}
