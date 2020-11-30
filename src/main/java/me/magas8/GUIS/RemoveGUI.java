package me.magas8.GUIS;

import com.cryptomorin.xseries.XMaterial;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotIterator;
import fr.minuskube.inv.content.SlotPos;
import me.magas8.CustomHeads.skull;
import me.magas8.Enums.GuiTypes;
import me.magas8.Hooks.FactionHook;
import me.magas8.LunarSandBot;
import me.magas8.Managers.ItemBuilder;
import me.magas8.Managers.SandBot;
import me.magas8.Utils.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class RemoveGUI extends MenuManager implements InventoryProvider {
    private  SmartInventory sandbtsinv;
    private LunarSandBot plugin;
    private SandBot bot;
    FactionHook factionHook;

    public RemoveGUI(LunarSandBot plugin,SandBot bot){
        this.plugin=plugin;
        this.factionHook= (FactionHook)this.plugin.getHookManager().getPluginMap().get("Factions");
        this.bot=bot;
        this.sandbtsinv = SmartInventory.builder()
                .id("confirm")
                .provider(this)
                .size(3, 9)
                .manager(LunarSandBot.getInvManager())
                .title(utils.color(plugin.getConfig().getString("confirm-gui-title")))
                .build();
    }
    public RemoveGUI open(Player player){
        openPlayer(player);
        return this;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ItemStack purpleglass = new ItemBuilder(XMaterial.valueOf(plugin.getConfig().getString("confirm-gui-glass-fill").toUpperCase()).parseItem()).setName(" ").toItemStack();
        int[] purplec = {1,3,5,7};
        int[] purpler = {1,1,1,1};
        for (int i = 0; i < purplec.length; i++) {
            SlotPos slot = new SlotPos(purpler[i],purplec[i]);
            contents.set(slot, ClickableItem.empty(purpleglass));
        }
        contents.newIterator("iter1", SlotIterator.Type.HORIZONTAL, 0, 4);
        contents.newIterator("iter2", SlotIterator.Type.HORIZONTAL, 2, 4);
        contents.newIterator("iter3", SlotIterator.Type.HORIZONTAL, 2, 4);
        contents.newIterator("iter4", SlotIterator.Type.HORIZONTAL, 0, 4);
        ItemStack glass = new ItemBuilder(XMaterial.valueOf(plugin.getConfig().getString("confirm-gui-glass-from").toUpperCase()).parseItem()).setName(" ").toItemStack();
        contents.fillBorders(ClickableItem.empty(glass));
        //Profile
        ItemStack mainhead = utils.botItem(plugin.getConfig().getString("bot-profile"), plugin.getConfig().getStringList("bot-profile-lore"), bot);
        contents.set(1,4,ClickableItem.empty(mainhead));

        //2 1 accept
        ItemStack accept = utils.botItem(plugin.getConfig().getString("bot-confirm-button-name"), plugin.getConfig().getStringList("bot-confirm-button-lore"), bot, skull.getCustomSkull("http://textures.minecraft.net/texture/"+plugin.getConfig().getString("bot-confirm-button-texture")));
        contents.set(1, 2, ClickableItem.of(accept, e -> {
            Player invplayer = (Player) e.getWhoClicked();

            if (e.isLeftClick() || e.isRightClick()) {
                if(!factionHook.isFactionAdmin(player) && !bot.getOwnerUUID().equals(player.getUniqueId())){
                    invplayer.sendMessage(utils.color(plugin.getConfig().getString("not-bot-owner").replace("%botowner%",bot.getOwnerName())));
                    invplayer.closeInventory();
                    return;
                }
                //Check if inventory is valid
                if(LunarSandBot.botGuis.containsKey(bot)) {
                    ItemStack botdropItem = new ItemBuilder(XMaterial.matchXMaterial(plugin.getConfig().getString("bot-spawn-item-material")).get().parseItem()).setColoredName(plugin.getConfig().getString("bot-spawn-item-name")).setColoredLore(plugin.getConfig().getStringList("bot-spawn-item-lore")).toItemStack();
                    utils.dropitem(invplayer,bot.getLocation(),botdropItem);
                    Double cash = bot.getBalance();
                    if(cash > 0){
                        LunarSandBot.econ.depositPlayer(invplayer,cash);
                        invplayer.sendMessage(utils.color(plugin.getConfig().getString("cash-back").replace("%amount%",cash.toString())));
                    }
                    invplayer.sendMessage(utils.color(plugin.getConfig().getString("remove-succeed")));
                    invplayer.closeInventory();
                    utils.removeBot(bot);
                }else{
                    invplayer.closeInventory();
                }
            }
        }));
        ItemStack cancel = utils.botItem(plugin.getConfig().getString("bot-cancel-button-name"), plugin.getConfig().getStringList("bot-cancel-button-lore"), bot, skull.getCustomSkull("http://textures.minecraft.net/texture/"+plugin.getConfig().getString("bot-cancel-button-texture")));
        contents.set(1, 6, ClickableItem.of(cancel, e -> {
            Player invplayer = (Player) e.getWhoClicked();
            if (e.isLeftClick() || e.isRightClick()) {
                if(LunarSandBot.botGuis.containsKey(bot)) {
                    ((SandBotGui) LunarSandBot.botGuis.get(bot).get(GuiTypes.SANDBOTGUI)).open(invplayer);
                }else{
                    player.closeInventory();
                }
                invplayer.sendMessage(utils.color(plugin.getConfig().getString("remove-canceled")));
            }
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        int state = contents.property("state", 0);
        contents.setProperty("state", state + 1);
        ItemStack glasstmp = new ItemBuilder(XMaterial.valueOf(plugin.getConfig().getString("confirm-gui-glass-to")).parseItem()).setName(" ").toItemStack();
        if (state % plugin.getConfig().getInt("sand-spawn-speed") == 0) {
            ItemStack mainhead = utils.botItem(plugin.getConfig().getString("bot-profile"), plugin.getConfig().getStringList("bot-profile-lore"), bot);
            contents.set(1,4,ClickableItem.empty(mainhead));
        }

        /**
         * Animation part
         */
        if (state % 5 == 0 && state<=30){
            if(state == 25){
                contents.set(new SlotPos(0,0), ClickableItem.empty(glasstmp));
                contents.set(new SlotPos(2,0), ClickableItem.empty(glasstmp));
                contents.set(new SlotPos(0,8), ClickableItem.empty(glasstmp));
                contents.set(new SlotPos(2,8), ClickableItem.empty(glasstmp));
                return;
            }
            if(state == 30){
                contents.set(new SlotPos(1,0), ClickableItem.empty(glasstmp));
                contents.set(new SlotPos(1,8), ClickableItem.empty(glasstmp));
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
    void openPlayer(Player player) {
        sandbtsinv.open(player);
    }
}
