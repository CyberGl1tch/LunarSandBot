package me.magas8;
import fr.minuskube.inv.InventoryManager;
import me.magas8.Commands.command;
import me.magas8.ConfigManager.FilesManager;
import me.magas8.Enums.GuiTypes;
import me.magas8.GUIS.MenuManager;
import me.magas8.Listeners.AntiDupeListener;
import me.magas8.Listeners.FactionListeners;
import me.magas8.Listeners.FactionsXListeners;
import me.magas8.Listeners.PlayerListeners;
import me.magas8.Managers.HookManager;
import me.magas8.Managers.SandBot;
import me.magas8.Runnables.BlockSpawn;
import me.magas8.Utils.Updater;
import me.magas8.Utils.utils;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public final class LunarSandBot extends JavaPlugin {
    public static Set<SandBot> sandBots = ConcurrentHashMap.newKeySet();
    public static ConcurrentHashMap<SandBot,ConcurrentHashMap<GuiTypes, MenuManager>> botGuis = new ConcurrentHashMap<>();
    private FilesManager manager = new FilesManager(this);
    private static InventoryManager invManager;
    public static Economy econ = null;
    public static Updater.Result updateResult = null;
    public static String version = null;
    public static Integer pluginID = 82608;
    private HookManager hookManager;


    @Override
    public void onEnable() {
        invManager = new InventoryManager(this);
        invManager.init();
        this.hookManager = new HookManager(this);
        new AntiDupeListener(this);
        utils.setupConfigFiles(manager);
        utils.loadDataOfSandbots(manager);
        new command(this);
        new PlayerListeners(this);
        new FactionListeners(this);
        new FactionsXListeners(this);
        UpdateCheck();
        setupEconomy();
        Metrics();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> Bukkit.getScheduler().runTask(this, new BlockSpawn(this)), 20L, this.getConfig().getInt("sand-spawn-speed"));
        }

    @Override
    public void onDisable() {
        utils.saveDataOfSandbots(manager);
    }

    public FilesManager getManager() {
        return manager;
    }
    public static InventoryManager getInvManager() {
        return invManager;
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            this.getServer().getConsoleSender().sendMessage(utils.color("&4&lVault is needed please make sure that you have it in your plugins folder"));
            this.getPluginLoader().disablePlugin(this);
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    private void Metrics(){
        int pluginId = 8453; //
        Metrics metrics = new Metrics(this, pluginId);
    }
    public File getPluginFile(){
        return this.getFile();
    }
    private void UpdateCheck(){
        Updater updater = new Updater(this, pluginID, this.getFile(), Updater.UpdateType.VERSION_CHECK, false);
        updateResult = updater.getResult();
        version=updater.getVersion();
    }
    public HookManager getHookManager() {
        return this.hookManager;
    }
}
