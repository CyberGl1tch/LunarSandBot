package me.magas8.Managers;


import me.magas8.LunarSandBot;
import me.magas8.Utils.utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class SandBot {
    private ArmorStand sandbotModel;
    private UUID sandbotUUID;
    private UUID ownerUUID;
    private ArrayList<Block> lapisBlocks;
    private Double balance;
    private Location location;
    private boolean isActivated;
    private String factionID;
    private boolean animate;
    private String ownerName;

    public SandBot(ArmorStand stand, Player player) {
        this.sandbotModel = stand;
        this.ownerUUID=player.getUniqueId();
        this.sandbotUUID=stand.getUniqueId();
        this.location=stand.getLocation();
        this.isActivated=false;
        this.factionID = utils.getFactionID(player);
        this.ownerName=player.getName();
        this.balance = 0.0;
        this.animate=false;
        this.lapisBlocks = utils.getNearbyBlocks(this);
    }
    public SandBot(UUID sandbotUUID, UUID ownerUUID,String ownerName, ArrayList<Block> lapisBlocks, Double balance, Location location, boolean isActivated, String factionID,boolean animate) {
        this.sandbotUUID = sandbotUUID;
        this.ownerUUID = ownerUUID;
        this.ownerName=ownerName;
        this.lapisBlocks = lapisBlocks;
        this.balance = balance;
        this.location = location;
        this.isActivated = isActivated;
        this.factionID=factionID;
        this.sandbotModel = (ArmorStand) utils.getEntityByUUID(sandbotUUID);
        this.animate=animate;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public ArrayList<Block> getLapisBlocks() {
        return lapisBlocks;
    }

    public void setLapisBlocks(ArrayList<Block> lapisBlocks) {
        this.lapisBlocks = lapisBlocks;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public UUID getSandbotUUID() {
        return sandbotUUID;
    }

    public void setSandbotUUID(UUID sandbotUUID) {
        this.sandbotUUID = sandbotUUID;
    }

    public ArmorStand getSandbotModel() {
        return sandbotModel;
    }

    public void setSandbotModel(ArmorStand sandbotModel) {
        this.sandbotModel = sandbotModel;
    }


    public String getFactionID() {
        return factionID;
    }

    public void setFactionID(String factionID) {
        this.factionID = factionID;
    }

    public boolean isAnimate() {
        return animate;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
        if(utils.getArmorAnimateBoolean() && sandbotModel != null && sandbotModel.isValid( )&& !sandbotModel.isDead()) {
            if (animate) {
                sandbotModel.setChestplate(utils.getColorArmor(Material.LEATHER_CHESTPLATE, utils.enabledArmorColor));
                sandbotModel.setLeggings(utils.getColorArmor(Material.LEATHER_LEGGINGS, utils.enabledArmorColor));
                sandbotModel.setBoots(utils.getColorArmor(Material.LEATHER_BOOTS, utils.enabledArmorColor));
            } else {
                sandbotModel.setChestplate(utils.getColorArmor(Material.LEATHER_CHESTPLATE, utils.disabledArmorColor));
                sandbotModel.setLeggings(utils.getColorArmor(Material.LEATHER_LEGGINGS, utils.disabledArmorColor));
                sandbotModel.setBoots(utils.getColorArmor(Material.LEATHER_BOOTS, utils.disabledArmorColor));
            }
        }
    }
    public void removeBlock(Block block){
        this.lapisBlocks.remove(block);
    }
    public void addBlock(Block block){
        this.lapisBlocks.add(block);
    }
    public void addBalance(Double balance){
        this.balance+=balance;
    }
    public void removeBalance(Double balance){
        this.balance-=balance;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
