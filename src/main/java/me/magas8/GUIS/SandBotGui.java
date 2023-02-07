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
import me.magas8.LunarSandBot;
import me.magas8.Managers.ItemBuilder;
import me.magas8.Managers.SandBot;
import me.magas8.Utils.utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;



public class SandBotGui extends MenuManager implements InventoryProvider {
    private SmartInventory sandbtsinv;
    private LunarSandBot plugin;
    private SandBot bot;

    public SandBotGui(LunarSandBot plugin,SandBot bot){
        this.plugin=plugin;
        this.bot=bot;
        this.sandbtsinv = SmartInventory.builder()
                .id("mainInv")
                .provider(this)
                .size(5, 9)
                .manager(LunarSandBot.getInvManager())
                .title(utils.color(plugin.getConfig().getString("main-gui-title")))
                .build();
    }
    public SandBotGui open(Player player){
        openPlayer(player);
        return this;
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        contents.newIterator("iter1", SlotIterator.Type.HORIZONTAL, 0, 0);
        contents.newIterator("iter2", SlotIterator.Type.HORIZONTAL, 4, 8);
        ItemStack glass = new ItemBuilder(XMaterial.valueOf(plugin.getConfig().getString("main-gui-glass-from")).parseItem()).setName(" ").toItemStack();
        ItemStack pinkglass = new ItemBuilder(XMaterial.valueOf(plugin.getConfig().getString("main-gui-glass-fill1")).parseItem()).setName(" ").toItemStack();
        ItemStack purpleglass = new ItemBuilder(XMaterial.valueOf(plugin.getConfig().getString("main-gui-glass-fill2")).parseItem()).setName(" ").toItemStack();

        //todo Maybe make that function in utils
        int[] pinkidc = {1,3,5,7,1,3,5,7};
        int[] pinkidr = {1,1,1,1,3,3,3,3};
        int[] purplec = {0,2,4,6,8,0,2,4,6,8,0,2,4,6,8};
        int[] purpler = {1,1,1,1,1,2,2,2,2,2,3,3,3,3,3};

        for (int i = 0; i < pinkidc.length; i++) {
            SlotPos slot = new SlotPos(pinkidr[i],pinkidc[i]);
            contents.set(slot, ClickableItem.empty(pinkglass));
        }
        for (int i = 0; i < purplec.length; i++) {
            SlotPos slot = new SlotPos(purpler[i],purplec[i]);
            contents.set(slot, ClickableItem.empty(purpleglass));
        }
        contents.fillRow(0, ClickableItem.empty(glass));
        contents.fillRow(4, ClickableItem.empty(glass));
        //ProfileItem
        ItemStack mainhead = utils.botItem(plugin.getConfig().getString("bot-profile"), plugin.getConfig().getStringList("bot-profile-lore"), bot);
        contents.set(2,1,ClickableItem.empty(mainhead));
        //GoldItem

        updateGold(player,contents,this.bot);
        //RemoveButton
        ItemStack remove = utils.botItem(plugin.getConfig().getString("bot-remove-button-name"), plugin.getConfig().getStringList("bot-remove-button-lore"), bot, skull.getCustomSkull("http://textures.minecraft.net/texture/"+plugin.getConfig().getString("bot-remove-button-texture")));
        contents.set(2, 7, ClickableItem.of(remove, e -> {
            Player invplayer = (Player) e.getWhoClicked();
            if (e.isLeftClick() || e.isRightClick()) {
                if(LunarSandBot.botGuis.containsKey(bot)) {
                    ((RemoveGUI) LunarSandBot.botGuis.get(bot).get(GuiTypes.REMOVEGUI)).open(invplayer);
                }else{
                    player.closeInventory();
                }
            }
        }));
        //CoreItem
        updateCore(player,contents,bot);
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        int state = contents.property("state", 0);
        contents.setProperty("state", state + 1);
        ItemStack glasstmp = new ItemBuilder(XMaterial.valueOf(plugin.getConfig().getString("main-gui-glass-to")).parseItem()).setName(" ").toItemStack();
        /**
         * Update Stats Part
         */
        if (state % plugin.getConfig().getInt("sand-spawn-speed") == 0) {
            updateCore(player,contents,bot);
            updateGold(player,contents,bot);
            ItemStack mainhead = utils.botItem(plugin.getConfig().getString("bot-profile"), plugin.getConfig().getStringList("bot-profile-lore"), bot);
            contents.set(2,1,ClickableItem.empty(mainhead));
        }

        /**
         * Animation Part
         */
        if (state % 3 == 0 && state<=27){
            if(state == 27){
                contents.set(new SlotPos(4,0), ClickableItem.empty(glasstmp));
                contents.set(new SlotPos(0,8), ClickableItem.empty(glasstmp));
                return;
            }
            SlotIterator iter = contents.iterator("iter1").get();
            SlotIterator iter2 = contents.iterator("iter2").get();
            iter.set(ClickableItem.empty(glasstmp)).next();
            iter2.set(ClickableItem.empty(glasstmp)).previous();

        }

    }
    private  void updateGold(Player player,InventoryContents contents,SandBot bot){
        ItemStack gold = utils.botItem(plugin.getConfig().getString("bot-balance-button-name"), plugin.getConfig().getStringList("bot-balance-button-lore"), bot, skull.getCustomSkull("http://textures.minecraft.net/texture/"+plugin.getConfig().getString("bot-balance-button-texture")));
        contents.set(2, 5, ClickableItem.of(gold, e -> {
            Player invplayer = (Player) e.getWhoClicked();
            double balanceplayer = LunarSandBot.econ.getBalance(player);
            double balancebot = bot.getBalance();
            double howmanytotake = (double) plugin.getConfig().getDouble("money-to-add");
            if(LunarSandBot.botGuis.containsKey(bot)) {
                if (e.isLeftClick()) {

                    if (balanceplayer >= howmanytotake) {
                        LunarSandBot.econ.withdrawPlayer(invplayer, howmanytotake);
                        bot.addBalance(howmanytotake);
                        updateGold(invplayer, contents, bot);
                    } else {
                        invplayer.sendMessage(utils.color(plugin.getConfig().getString("insufficient-balance-player")));
                    }
                } else if (e.isRightClick()) {
                    if (balancebot > 0) {
                        LunarSandBot.econ.depositPlayer(invplayer, balancebot);
                        invplayer.sendMessage(utils.color(plugin.getConfig().getString("cash-back").replace("%amount%", bot.getBalance().toString())));
                        bot.removeBalance(bot.getBalance());
                        updateGold(invplayer, contents, bot);
                    } else {
                        invplayer.sendMessage(utils.color(plugin.getConfig().getString("insufficient-balance-bot")));
                    }
                }
            }else{
                invplayer.closeInventory();
            }
        }));
    }
    private  void updateCore(Player player, InventoryContents contents,SandBot bot){
        ItemStack core = utils.botItem(plugin.getConfig().getString("bot-core-button-name"), plugin.getConfig().getStringList("bot-core-button-lore"), bot, skull.getCustomSkull("http://textures.minecraft.net/texture/"+plugin.getConfig().getString("bot-core-button-texture")));
        contents.set(2, 3, ClickableItem.of(core, e -> {
            if (e.isLeftClick() || e.isRightClick()) {
                Player invplayer = (Player) e.getWhoClicked();
                if(bot.isActivated()){
                    bot.setActivated(false);
                    bot.setAnimate(false);
                }else {
                    bot.setActivated(true);
                    bot.setAnimate(true);
                }
                updateCore(invplayer,contents,bot);
            }
        }));
    }


    @Override
    public void openPlayer(Player player) {
        this.sandbtsinv.open(player);
    }
}
