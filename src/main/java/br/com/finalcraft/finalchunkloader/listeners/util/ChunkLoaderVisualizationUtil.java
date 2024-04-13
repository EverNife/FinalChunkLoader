package br.com.finalcraft.finalchunkloader.listeners.util;

import br.com.finalcraft.finalchunkloader.FinalChunkLoader;
import br.com.finalcraft.finalchunkloader.chunkloader.CChunkLoader;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChunkLoaderVisualizationUtil {

    private static Map<UUID,BukkitTask> currentVisualizations = new HashMap<UUID,BukkitTask>();

    public static void showCorners(CChunkLoader chunkLoader, Player player) {
        World world = chunkLoader.getWorld();

        for (int z = chunkLoader.getChunkZ() - chunkLoader.getRange(); z <= chunkLoader.getChunkZ() + chunkLoader.getRange(); z++) {
            for (int i = 0; i < 16; i+=5) {
                for (int y = 0; y < 255; y+=40) {
                    player.sendBlockChange(new Location(world, ((chunkLoader.getChunkX() - chunkLoader.getRange())<<4), y, (z<<4)+i), Material.GLASS, (byte) 0);
                    player.sendBlockChange(new Location(world, ((chunkLoader.getChunkX() + chunkLoader.getRange())<<4)+15, y, (z<<4)+i), Material.GLASS, (byte) 0);
                }
            }
        }

        for (int x = chunkLoader.getChunkX() - chunkLoader.getRange(); x <= chunkLoader.getChunkX() + chunkLoader.getRange(); x++) {
            for (int i = 0; i < 16; i+=5) {
                for (int y = 0; y < 255; y+=40) {
                    player.sendBlockChange(new Location(world, (x<<4)+i, y, ((chunkLoader.getChunkZ() - chunkLoader.getRange())<<4)), Material.GLASS, (byte) 0);
                    player.sendBlockChange(new Location(world, (x<<4)+i, y, ((chunkLoader.getChunkZ() + chunkLoader.getRange())<<4)+15), Material.GLASS, (byte) 0);
                }
            }
        }

        BukkitTask v = currentVisualizations.put(player.getUniqueId(),
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.isOnline()) {
                            hideCorners(chunkLoader, player);
                        }
                    }
                }.runTaskLater(FinalChunkLoader.instance, 600L)
        );

        if (v != null) {
            v.cancel();
        }
    }

    public static void hideCorners(CChunkLoader chunkLoader, Player player) {
        World world = chunkLoader.getWorld();

        for (int z = chunkLoader.getChunkZ() - chunkLoader.getRange(); z <= chunkLoader.getChunkZ() + chunkLoader.getRange(); z++) {
            for (int i = 0; i < 16; i+=5) {
                for (int y = 0; y < 255; y+=40) {
                    Location l = new Location(world, ((chunkLoader.getChunkX() - chunkLoader.getRange())<<4), y, (z<<4)+i);
                    Block b = l.getBlock();
                    player.sendBlockChange(l, b.getType(), b.getData());
                    l.setX(((chunkLoader.getChunkX() + chunkLoader.getRange())<<4)+15);
                    b = l.getBlock();
                    player.sendBlockChange(l, b.getType(), b.getData());
                }
            }
        }

        for (int x = chunkLoader.getChunkX() - chunkLoader.getRange(); x <= chunkLoader.getChunkX() + chunkLoader.getRange(); x++) {
            for (int i = 0; i < 16; i+=5) {
                for (int y = 0; y < 255; y+=40) {
                    Location l = new Location(world, (x<<4)+i, y, ((chunkLoader.getChunkZ() - chunkLoader.getRange())<<4));
                    Block b = l.getBlock();
                    player.sendBlockChange(l, b.getType(), b.getData());

                    l.setZ(((chunkLoader.getChunkZ() + chunkLoader.getRange())<<4)+15);
                    b = l.getBlock();
                    player.sendBlockChange(l, b.getType(), b.getData());
                }
            }
        }
    }

}
