package br.com.finalcraft.finalchunkloader.integration;

import br.com.finalcraft.evernifecore.cooldown.Cooldown;
import br.com.finalcraft.evernifecore.integration.placeholders.PAPIIntegration;
import br.com.finalcraft.evernifecore.minecraft.worlddata.BlockMetaData;
import br.com.finalcraft.finalchunkloader.chunkloader.CChunkLoader;
import br.com.finalcraft.finalchunkloader.config.data.FCLPlayerData;
import br.com.finalcraft.finalchunkloader.config.datastore.ChunkLoaderManager;
import br.com.finalcraft.finalchunkloader.config.settings.ChunkLoaderType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;

public class PlaceHolderIntegration {

    public static void initialize(JavaPlugin plugin){
        PAPIIntegration.createPlaceholderIntegration(plugin, "bcl", FCLPlayerData.class)
                .addParser("chunks_total", playerData -> ChunkLoaderManager.getChunkloaderDatabase())
                .addParser("chunks_active", playerData -> activeChunks(null))
                .addParser("chunks_commmon_active", playerData -> activeChunks(ChunkLoaderType.ONLINE_ONLY))
                .addParser("chunks_premium_active", playerData -> activeChunks(ChunkLoaderType.ALWAYS_ON))

                .addParser("common_chunks", playerData -> playerData.getChunkLoadersInUseCount(ChunkLoaderType.ONLINE_ONLY))
                .addParser("premium_chunks", playerData -> playerData.getChunkLoadersInUseCount(ChunkLoaderType.ALWAYS_ON));
    }

    private static WeakReference<List<CChunkLoader>> ALL_CHUNK_LOADERS;
    private static Cooldown ALL_CHUNK_LOADERS_LAST_RETRIEVAL = new Cooldown.GenericCooldown("");

    private static long activeChunks(@Nullable ChunkLoaderType chunkLoaderType){
        if (ALL_CHUNK_LOADERS.get() == null || !ALL_CHUNK_LOADERS_LAST_RETRIEVAL.isInCooldown()) {
            ALL_CHUNK_LOADERS_LAST_RETRIEVAL.startWith(5);
            ALL_CHUNK_LOADERS = new WeakReference<>(ChunkLoaderManager.getChunkloaderDatabase().getAllBlockMetaData().stream()
                    .map(BlockMetaData::getValue)
                    .collect(Collectors.toList()));
        }

        if (chunkLoaderType == null){
            return ALL_CHUNK_LOADERS.get().size();
        }

        return ALL_CHUNK_LOADERS.get().stream()
                .filter(cChunkLoader -> cChunkLoader.getChunkLoaderType() == chunkLoaderType)
                .count();
    }
}
