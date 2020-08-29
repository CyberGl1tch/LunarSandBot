package me.magas8.Runnables;


import me.magas8.LunarSandBot;
import me.magas8.Managers.SandBot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

public class BlockSpawn implements Runnable {
    private LunarSandBot plugin;
    public BlockSpawn(LunarSandBot plugin){
        this.plugin=plugin;

    }
    public void run() {
        for (SandBot bot : LunarSandBot.sandBots) {
            if (!bot.isActivated())
                continue;
            if (bot.getBalance() < (bot.getLapisBlocks().size() * plugin.getConfig().getDouble("sand-cost")) || bot.getLapisBlocks().isEmpty()) {
                bot.setActivated(false);
                bot.setAnimate(false);
                continue;
            }
            boolean stopAnimation = true;
            for (Block block : bot.getLapisBlocks()) {
                if (block.getLocation().getBlock().getType() != Material.valueOf(plugin.getConfig().getString("target-block").toUpperCase())) {
                    Bukkit.getServer().getScheduler().runTask(plugin,()->{
                        bot.removeBlock(block.getLocation().getBlock());
                    });
                     continue;
                }

                //Continue if under block is non air
                if (block.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR){
                    continue;
                }
                stopAnimation = false;
                //Enable animation if there is air Under the target block
                if(!bot.isAnimate()){
                    bot.setAnimate(true);
                }
                Location location = block.getLocation();
                location.add(0.0D, -1.0D, 0.0D);
                FallingBlock falling = bot.getLocation().getWorld().spawnFallingBlock(location, Material.SAND, (byte)0);
                falling.setDropItem(false);
                falling.setVelocity(new Vector(0, -plugin.getConfig().getInt("fall-speed"), 0));
                bot.removeBalance(plugin.getConfig().getDouble("sand-cost"));
            }

            if(stopAnimation){
                bot.setAnimate(false);
            }
        }
    }
}
