package br.com.finalcraft.finalchunkloader.config.settings;

import br.com.finalcraft.evernifecore.itemstack.ComparableItem;
import br.com.finalcraft.evernifecore.itemstack.FCItemFactory;
import br.com.finalcraft.finalchunkloader.config.ConfigManager;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class FCLSettings {

    public static int MAX_HOURS_OFFLINE;

    public static int DEFAULT_CHUNK_AMOUNT_ALWAYSON;
    public static int DEFAULT_CHUNK_AMOUNT_ONLINEONLY;
    public static int MAX_CHUNK_AMOUNT_ALWAYSON;
    public static int MAX_CHUNK_AMOUNT_ONLINEONLY;

    public static ComparableItem ONLINE_ONLY_BLOCK;
    public static ComparableItem ALWAYS_ON_BLOCK;
    public static ComparableItem INTERACTION_ROD;

    public static boolean WILDCARD_WORLD = false;
    public static Set<String> WORLDS_WHITELIST;

    public static boolean ENABLED = true;

    public static void initialize(){

        MAX_HOURS_OFFLINE = ConfigManager.getMainConfig().getOrSetDefaultValue(
                "Settings.maxHoursOffline",
                168,
                "After how many hours after a player's last login his 'AlwaysOn Chunkloaders' will be disabled."
        );

        WORLDS_WHITELIST = ConfigManager.getMainConfig().getOrSetDefaultValue(
                "Settings.enabledWorlds",
                new ArrayList<>(Arrays.asList("*")),
                "The worlds where the plugin is enabled."
        ).stream().collect(Collectors.toSet());
        WILDCARD_WORLD = WORLDS_WHITELIST.contains("*");

        DEFAULT_CHUNK_AMOUNT_ALWAYSON = ConfigManager.getMainConfig().getOrSetDefaultValue(
                "PerPlayerChunksConfiguration.Default.alwaysOn",
                9,
                "Default amount of ChunkLoaders of the type 'ALWAYS_ON' a player STARTS with."
        );

        DEFAULT_CHUNK_AMOUNT_ONLINEONLY = ConfigManager.getMainConfig().getOrSetDefaultValue(
                "PerPlayerChunksConfiguration.Default.onlineOnly",
                0,
                "Default amount of ChunkLoaders of the type 'ONLINE_ONLY' a player STARTS with."
        );

        MAX_CHUNK_AMOUNT_ALWAYSON = ConfigManager.getMainConfig().getOrSetDefaultValue(
                "PerPlayerChunksConfiguration.Max.alwaysOn",
                9,
                "Max amount of ChunkLoaders of the type 'ALWAYS_ON' a player can have."
        );

        MAX_CHUNK_AMOUNT_ONLINEONLY = ConfigManager.getMainConfig().getOrSetDefaultValue(
                "PerPlayerChunksConfiguration.Max.onlineOnly",
                49,
                "Max amount of ChunkLoaders of the type 'ONLINE_ONLY' a player can have."
        );

        ONLINE_ONLY_BLOCK = ConfigManager.getMainConfig().getOrSetDefaultValue(
                "ChukLoaderBlock.ONLINE_ONLY",
                new ComparableItem(FCItemFactory.from("IRON_BLOCK").build()),
                "The block that represents the 'ONLINE_ONLY' ChunkLoaders."
        );

        ALWAYS_ON_BLOCK = ConfigManager.getMainConfig().getOrSetDefaultValue(
                "ChukLoaderBlock.ALWAYS_ON",
                new ComparableItem(FCItemFactory.from("DIAMOND_BLOCK").build()),
                "The block that represents the 'ALWAYS_ON' ChunkLoaders."
        );

        INTERACTION_ROD = ConfigManager.getMainConfig().getOrSetDefaultValue(
                "ChukLoaderBlock.INTERACTION_ROD",
                new ComparableItem(FCItemFactory.from(Material.BLAZE_ROD).build()),
                "The item that allows interacting with the ChunkLoaders."
        );

        ConfigManager.getMainConfig().saveIfNewDefaults();
    }

    public static ChunkLoaderType getChunkLoaderType(Block block){
        if (ONLINE_ONLY_BLOCK.match(block)){
            return ChunkLoaderType.ONLINE_ONLY;
        }
        if (ALWAYS_ON_BLOCK.match(block)){
            return ChunkLoaderType.ALWAYS_ON;
        }
        return null;
    }

    public static boolean isWorldEnabled(String worldName){
        return WILDCARD_WORLD || WORLDS_WHITELIST.contains(worldName);
    }

}
