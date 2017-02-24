/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stuff.applicationavatarmc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This plugin will drop (10-x)*10% of the items in the inventory where x is the
 * amount of invetoryKeeper items, and keep all other items that are left in the
 * inventory.
 * @author stefanberg96
 */
public class inventoryKeeperOnDeath extends JavaPlugin implements Listener {

    private static final Material INVENTORY_KEEPER_ITEM = Material.ENDER_PORTAL_FRAME;

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent playerDeath) {
        Player player = playerDeath.getEntity();
        PlayerInventory inventory = playerDeath.getEntity().getInventory();
        final ItemStack itemStack = new ItemStack(INVENTORY_KEEPER_ITEM);
        HashMap<Integer, ItemStack> inventoryKeeperStack
                = (HashMap<Integer, ItemStack>) inventory.all(INVENTORY_KEEPER_ITEM);

        if (!inventoryKeeperStack.isEmpty()) {
            int amount = countAmountOfKeeperItems(inventoryKeeperStack);
            double percentageToRemove = (10 - Math.min(10, amount)) * 0.1;
            ItemStack[] items = inventory.getContents();
            int amountItemsToDrop = (int) percentageToRemove * items.length;
            removeItems(amountItemsToDrop, inventory);
            playerDeath.setKeepInventory(true);
        } else {
            playerDeath.setKeepInventory(false);
        }

    }

    /**
     * randomly chooses an item in the inventory and removes it, it doesn't
     * remove any of the inventoryKeeper items
     *
     * @param amountItemsToDrop the amount of items that have to be dropped
     * @param items the inventory of the player
     */
    private void removeItems(int amountItemsToDrop, PlayerInventory inventory) {
        //I assume that you won't drop inventoryKeeper items on death
        Random random = new Random();
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < amountItemsToDrop; i++) {
            int index = random.nextInt(items.length);
            int j;
            for (j = 0; j < items.length; j++) {
                if (items[j % items.length] != null
                        && items[j % items.length].getType() != INVENTORY_KEEPER_ITEM) {
                    inventory.remove(items[j % items.length]);
                    items[j % items.length] = null;
                    break;
                }
            }
            //can't remove any more items all other items are
            //inventoryKeeper
            if (j >= items.length) {
                break;
            }
        }
    }

    /**
     * Counts the amount of Keeper items the player has, and also remove one
     * from the player
     *
     * @param inventoryKeeperStack HashMap containing the index in the inventory
     * and itemStack containing only inventoryKeeper material
     * @return the total amount of keeper items
     */
    @SuppressWarnings("null")
    private int countAmountOfKeeperItems(
            HashMap<Integer, ItemStack> inventoryKeeperStack) {
        Collection<ItemStack> itemStacks = inventoryKeeperStack.values();
        int amount = 0;
        ItemStack removeFrom = null;
        for (ItemStack is : itemStacks) {
            removeFrom = is;
            amount += is.getAmount();
        }
        //also remove one keeper item
        removeFrom.setAmount(removeFrom.getAmount() - 1);
        return amount;
    }
}
