package net.piratjsk.portals;

import net.piratjsk.portals.store.SQLITE;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.Dispenser;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import java.io.IOException;

public final class Portals extends JavaPlugin {

    private static Portals instance;
    private PortalsManager portalsManager;
    private final static Material[] portalRecipe = { Material.REDSTONE_BLOCK, Material.ENDER_PEARL,  Material.REDSTONE_BLOCK,
                                                     Material.ENDER_PEARL,    Material.EYE_OF_ENDER, Material.ENDER_PEARL,
                                                     Material.REDSTONE_BLOCK, Material.ENDER_PEARL,  Material.REDSTONE_BLOCK};
    public static String activePortalsMsg;
    private SQLITE sqlite;

    @Override
    public void onEnable() {
        instance = this;
        sqlite = new SQLITE();
        // config
        saveDefaultConfig();
        activePortalsMsg = getConfig().getString("active-portals-msg");

        // portals manager
        this.portalsManager = new PortalsManager(this);
        getServer().getPluginManager().registerEvents(new PortalsListener(this), this);

        // 'portals' command
        getCommand("portals").setExecutor(new PortalsCommand(this));

        // plugin metrics
        try {
            final MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (final IOException ignored) {}
    }

    @Override
    public void onDisable() {
        portalsManager.save();
    }

    public static boolean isValidPortal(final Location loc) {
        final Block block = loc.getBlock();
        if (block.getType().equals(Material.DISPENSER))
            if (block.getState() instanceof org.bukkit.block.Dispenser)
                return isValidPortal(((org.bukkit.block.Dispenser)block.getState()).getInventory());
        return false;
    }

    public static boolean isValidPortal(final Inventory inv) {
        org.bukkit.block.Dispenser dis = (org.bukkit.block.Dispenser) inv.getHolder();
        Dispenser dispenser = (Dispenser) dis.getData();
        if (dispenser.getFacing().equals(BlockFace.UP)) {
            for (int i=0; i<9; ++i) {
                if (inv.getItem(i)==null || !inv.getItem(i).getType().equals(portalRecipe[i])) return false;
                if (i==4)
                    if (!inv.getItem(i).getItemMeta().hasDisplayName())
                        return false;
            }
            return true;
        }
        return false;
    }

    public static String getPortalName(final Inventory inv) {
        return inv.getItem(4).getItemMeta().getDisplayName();
    }

    public PortalsManager getPortalsManager() {
        return this.portalsManager;
    }

    public static Portals getInstance() {
        return instance;
    }

}
