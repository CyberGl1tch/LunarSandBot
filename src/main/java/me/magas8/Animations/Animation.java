package me.magas8.Animations;
import me.magas8.LunarSandBot;
import me.magas8.Managers.SandBot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

public class Animation extends BukkitRunnable {
    private Integer flag = 0;
    private SandBot bot ;
    private LunarSandBot plugin;
    public Animation(SandBot bot, LunarSandBot plugin){
        this.bot=bot;
        this.plugin=plugin;
    }
    @Override
    public void run() {
            if(!plugin.getConfig().getBoolean("bot-animate-hand")) this.cancel();
            if(bot.getSandbotModel() == null || bot.getSandbotModel().isDead() || !bot.getSandbotModel().isValid()) this.cancel();
            if(!bot.isAnimate()) return;

            EulerAngle old = bot.getSandbotModel().getRightArmPose();
            if (Math.abs(old.getX()) >= 1 && flag == 0) {//-150 eos -100

                EulerAngle newe = old.add(0.05f, 0.05f, 0.02f);
                bot.getSandbotModel().setRightArmPose(newe);
            } else {
                flag = 1;
                if (Math.abs(old.getX()) < 2.5f && flag == 1) {
                    EulerAngle newe = old.add(-0.05f, -0.05f, -0.02f);
                    bot.getSandbotModel().setRightArmPose(newe);
                } else {
                    flag = 0;
                }
            }

    }
}
