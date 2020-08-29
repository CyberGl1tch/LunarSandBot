package me.magas8.Listeners;

import me.magas8.LunarSandBot;
import me.magas8.Managers.SandBot;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class AntiDupeListener implements Listener {
    private LunarSandBot plugin;
    public AntiDupeListener(LunarSandBot plugin){
        this.plugin=plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void OnChunkUnload(ChunkUnloadEvent e){
        Chunk sandbtChunk = e.getChunk();
        outer:
        for(Entity tmp : sandbtChunk.getEntities()){
            if(tmp.getType().equals(EntityType.ARMOR_STAND)){
                for(SandBot bot : LunarSandBot.sandBots){
                    if(bot.getSandbotUUID().equals(tmp.getUniqueId())){
                        e.setCancelled(true);
                        break outer;
                    }
                }
            }
        }
    }
}
