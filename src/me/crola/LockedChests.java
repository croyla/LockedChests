package me.crola;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class LockedChests extends JavaPlugin implements Listener {

    NamespacedKey dataKey;
    @Override
    public void onEnable() {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        dataKey = new NamespacedKey(this, "prot");
        meta.getPersistentDataContainer().set(dataKey, PersistentDataType.BYTE, (byte)1);
        meta.setDisplayName("§rLocked Chest");
        item.setItemMeta(meta);
        NamespacedKey key = new NamespacedKey(this, "protected_chest");

        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("L", "C", "I");
        recipe.setIngredient('L', Material.LEVER);
        recipe.setIngredient('C', Material.CHEST);
        recipe.setIngredient('I', Material.IRON_INGOT);
        Bukkit.addRecipe(recipe);

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        if(event.getBlockPlaced().getType() == Material.CHEST)
            if(event.getItemInHand().getItemMeta().getPersistentDataContainer().has(dataKey, PersistentDataType.BYTE)) {
                Chest chest = ((Chest) event.getBlockPlaced().getState());
                NamespacedKey key = new NamespacedKey(this, "lock-owner");
                chest.getPersistentDataContainer().set(key, PersistentDataType.STRING, event.getPlayer().getUniqueId().toString());
                chest.update();
            }

    }
    @EventHandler
    public void onOpen(PlayerInteractEvent event){

        if(event.hasBlock())
            if(event.getClickedBlock().getType() == Material.CHEST){
                Chest chest = ((Chest) event.getClickedBlock().getState());
                NamespacedKey key = new NamespacedKey(this, "lock-owner");
                if(chest.getPersistentDataContainer().has(key, PersistentDataType.STRING))
                    if (!chest.getPersistentDataContainer().get(key, PersistentDataType.STRING).equals(event.getPlayer().getUniqueId().toString()))
                        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            event.setCancelled(true);
                            TextComponent component = new TextComponent("This chest is locked!");
                            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
                        }


            }

    }
}
