package me.magas8.GUIS;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FPlayer;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.*;
import me.magas8.Enums.GuiTypes;
import me.magas8.Hooks.FactionHook;
import me.magas8.LunarSandBot;
import me.magas8.Managers.ItemBuilder;
import me.magas8.Managers.SandBot;
import me.magas8.Utils.utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class FactionManageGui extends MenuManager implements InventoryProvider {
    private  SmartInventory sandbtsinv;
    private LunarSandBot plugin;
    private ArrayList<SlotPos> botSlots = new ArrayList<>();
    private  ClickableItem[] panginationItems;
    private SlotPos nextPageSlot;
    private SlotPos prevPageSlot;
    FactionHook factionHook;

    public FactionManageGui(LunarSandBot plugin){
        this.plugin=plugin;
        this.factionHook= (FactionHook)this.plugin.getHookManager().getPluginMap().get("Factions");
        this.sandbtsinv = SmartInventory.builder()
                .id("sandbots")
                .provider(this)
                .size(27/9, 9)
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
        ItemStack glass = new ItemBuilder(XMaterial.valueOf(plugin.getConfig().getString("sandbots-gui-glass-fill").toUpperCase()).parseItem()).setName(" ").toItemStack();
        contents.fillBorders(ClickableItem.empty(glass));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();
        this.panginationItems= new ClickableItem[(int)LunarSandBot.sandBots.stream().filter(c-> c.getFactionID().equals(factionHook.getFactionId(player))).count()];
        AtomicInteger count = new AtomicInteger(0);
        LunarSandBot.sandBots.stream().filter(c-> c.getFactionID().equals(factionHook.getFactionId(player))).forEach(sandBot -> {
            ItemStack mainhead = utils.botItem(plugin.getConfig().getString("bot-profile"), plugin.getConfig().getStringList("bot-profile-lore"), sandBot);
            panginationItems[count.get()] = ClickableItem.of(mainhead,e -> {
                if (e.isLeftClick() || e.isRightClick()) {
                    Player invplayer = (Player) e.getWhoClicked();
                    if(LunarSandBot.botGuis.containsKey(sandBot)) {
                        ((SandBotGui) LunarSandBot.botGuis.get(sandBot).get(GuiTypes.SANDBOTGUI)).open(invplayer);
                    }else{
                        player.closeInventory();
                    }
                }
            });
            count.addAndGet(1);

        });
        pagination.setItems(this.panginationItems);
        pagination.setItemsPerPage(7);
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1));
        this.nextPageSlot = new SlotPos(plugin.getConfig().getInt("sandbots-faction-gui-next-page-slot") / 9, plugin.getConfig().getInt("sandbots-faction-gui-next-page-slot") % 9);
        if (XMaterial.matchXMaterial(plugin.getConfig().getString("sandbots-faction-gui-next-page-item").toUpperCase()).isPresent() && nextPageSlot.getColumn()<=contents.inventory().getColumns() && nextPageSlot.getRow()<=contents.inventory().getRows()) {
            ItemStack next= new ItemBuilder(XMaterial.matchXMaterial(plugin.getConfig().getString("sandbots-faction-gui-next-page-item").toUpperCase()).get().parseItem())
                    .setColoredName(plugin.getConfig().getString("sandbots-faction-gui-next-page-name").replace("%page%",String.valueOf(pagination.getPage()+1)).replace("%maxPage%",String.valueOf(((int) (panginationItems.length-1)/7)+1)))
                    .setColoredPagesLore(plugin.getConfig().getStringList("sandbots-faction-gui-next-page-lore"),String.valueOf(pagination.getPage()+1),String.valueOf(((int) (panginationItems.length-1)/7)+1)).toItemStack();

            contents.set(nextPageSlot, ClickableItem.of(next,
                    e -> sandbtsinv.open(player, pagination.next().getPage())));
        }
        this.prevPageSlot = new SlotPos(plugin.getConfig().getInt("sandbots-faction-gui-previous-page-slot") / 9, plugin.getConfig().getInt("sandbots-faction-gui-previous-page-slot") % 9);
        if (XMaterial.matchXMaterial(plugin.getConfig().getString("sandbots-faction-gui-previous-page-item").toUpperCase()).isPresent() && nextPageSlot.getColumn()<=contents.inventory().getColumns() && nextPageSlot.getRow()<=contents.inventory().getRows()) {
            ItemStack prev= new ItemBuilder(XMaterial.matchXMaterial(plugin.getConfig().getString("sandbots-faction-gui-previous-page-item").toUpperCase()).get().parseItem())
                    .setColoredName(plugin.getConfig().getString("sandbots-faction-gui-previous-page-name").replace("%page%",String.valueOf(pagination.getPage()+1)).replace("%maxPage%",String.valueOf(((int) (panginationItems.length-1)/7)+1)))
                    .setColoredPagesLore(plugin.getConfig().getStringList("sandbots-faction-gui-previous-page-lore"),String.valueOf(pagination.getPage()+1),String.valueOf(((int) (panginationItems.length-1)/7)+1)).toItemStack();

            contents.set(prevPageSlot, ClickableItem.of(prev,
                    e -> sandbtsinv.open(player, pagination.previous().getPage())));
        }

    }

    @Override
    public void openPlayer(Player player) {
        sandbtsinv.open(player);
    }
}
