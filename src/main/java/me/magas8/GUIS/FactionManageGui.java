package me.magas8.GUIS;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FPlayer;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotIterator;
import fr.minuskube.inv.content.SlotPos;
import me.magas8.Enums.GuiTypes;
import me.magas8.Hooks.FactionHook;
import me.magas8.LunarSandBot;
import me.magas8.Managers.ItemBuilder;
import me.magas8.Managers.SandBot;
import me.magas8.Utils.utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


import java.util.ArrayList;

public class FactionManageGui extends MenuManager implements InventoryProvider {
    private  SmartInventory sandbtsinv;
    private LunarSandBot plugin;
    private ArrayList<SlotPos> botSlots = new ArrayList<>();
    FactionHook factionHook;

    public FactionManageGui(LunarSandBot plugin){
        this.plugin=plugin;
        this.factionHook= (FactionHook)this.plugin.getHookManager().getPluginMap().get("Factions");
        this.sandbtsinv = SmartInventory.builder()
                .id("sandbots")
                .provider(this)
                .size(3, 9)
                .manager(LunarSandBot.getInvManager())
                .title(utils.color(plugin.getConfig().getString("sandbots-gui-title")))
                .build();
    }
    public FactionManageGui open(Player player){
        openPlayer(player);
        return this;
    }

    @Override
    public void init(Player player, InventoryContents contents) {

        ItemStack glass = new ItemBuilder(XMaterial.valueOf(plugin.getConfig().getString("sandbots-gui-glass-from").toUpperCase()).parseItem()).setName(" ").toItemStack();

        contents.newIterator("iter1", SlotIterator.Type.HORIZONTAL, 0, 4);
        contents.newIterator("iter2", SlotIterator.Type.HORIZONTAL, 2, 4);
        contents.newIterator("iter3", SlotIterator.Type.HORIZONTAL, 2, 4);
        contents.newIterator("iter4", SlotIterator.Type.HORIZONTAL, 0, 4);
        contents.fillBorders(ClickableItem.empty(glass));
        ItemStack fill = new ItemBuilder(XMaterial.valueOf(plugin.getConfig().getString("sandbots-gui-glass-fill").toUpperCase()).parseItem()).setName(" ").toItemStack();
        int botLimit = plugin.getConfig().getInt("faction-bot-limit");
        if(botLimit>7){
            botLimit=7;
        }
        for(int i= botLimit+1;  i<=7; i++){
            contents.set(1,i, ClickableItem.empty(fill));
        }
        for (SandBot bot : LunarSandBot.sandBots) {
            if(bot.getFactionID().equals(factionHook.getFactionId(player))) {
                ItemStack mainhead = utils.botItem(plugin.getConfig().getString("bot-profile"), plugin.getConfig().getStringList("bot-profile-lore"), bot);
                botSlots.add(contents.firstEmpty().get());
                contents.set(contents.firstEmpty().get(), ClickableItem.empty(mainhead));
            }
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        int state = contents.property("state", 0);
        contents.setProperty("state", state + 1);
        ItemStack glasstmp = new ItemBuilder(XMaterial.valueOf(plugin.getConfig().getString("sandbots-gui-glass-to").toUpperCase()).parseItem()).setName(" ").toItemStack();

        /**
         * SandBot Update Stats Part
         */
        if (state % plugin.getConfig().getInt("sand-spawn-speed") == 0) {

                int i = 0;
                for (SandBot bot : LunarSandBot.sandBots) {
                    if(bot.getFactionID().equals(factionHook.getFactionId(player))) {
                        ItemStack mainhead = utils.botItem(plugin.getConfig().getString("bot-profile"), plugin.getConfig().getStringList("bot-profile-lore"), bot);
                        contents.set(botSlots.get(i), ClickableItem.of(mainhead,e -> {
                            if (e.isLeftClick() || e.isRightClick()) {
                                Player invplayer = (Player) e.getWhoClicked();
                                if(LunarSandBot.botGuis.containsKey(bot)) {
                                    ((SandBotGui) LunarSandBot.botGuis.get(bot).get(GuiTypes.SANDBOTGUI)).open(invplayer);
                                }else{
                                    player.closeInventory();
                                }
                            }
                        }));
                        i++;
                    }
                }
        }
        /**
         * Animation Part
        */
        if (state % 5 == 0 && state <= 30) {
            if (state == 25) {
                contents.set(new SlotPos(0, 0), ClickableItem.empty(glasstmp));
                contents.set(new SlotPos(2, 0), ClickableItem.empty(glasstmp));
                contents.set(new SlotPos(0, 8), ClickableItem.empty(glasstmp));
                contents.set(new SlotPos(2, 8), ClickableItem.empty(glasstmp));
                return;

            }
            if (state == 30) {
                contents.set(new SlotPos(1, 0), ClickableItem.empty(glasstmp));
                contents.set(new SlotPos(1, 8), ClickableItem.empty(glasstmp));
                return;
            }
            SlotIterator iter = contents.iterator("iter1").get();
            SlotIterator iter2 = contents.iterator("iter2").get();
            SlotIterator iter3 = contents.iterator("iter3").get();
            SlotIterator iter4 = contents.iterator("iter4").get();

            iter.set(ClickableItem.empty(glasstmp)).next();
            iter2.set(ClickableItem.empty(glasstmp)).next();
            iter3.set(ClickableItem.empty(glasstmp)).previous();
            iter4.set(ClickableItem.empty(glasstmp)).previous();
        }


    }

    @Override
    public void openPlayer(Player player) {
        sandbtsinv.open(player);
    }
}
