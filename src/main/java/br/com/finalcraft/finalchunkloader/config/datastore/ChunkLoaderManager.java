package br.com.finalcraft.finalchunkloader.config.datastore;

import br.com.finalcraft.evernifecore.config.playerdata.PlayerController;
import br.com.finalcraft.evernifecore.minecraft.worlddata.BlockMetaData;
import br.com.finalcraft.evernifecore.minecraft.worlddata.ServerData;
import br.com.finalcraft.evernifecore.scheduler.FCScheduler;
import br.com.finalcraft.evernifecore.util.FCCollectionsUtil;
import br.com.finalcraft.finalchunkloader.FinalChunkLoader;
import br.com.finalcraft.finalchunkloader.chunkloader.CChunkLoader;
import br.com.finalcraft.finalchunkloader.config.data.FCLPlayerData;
import br.com.finalcraft.finalchunkloader.util.FCLForgeLibUtil;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChunkLoaderManager {

    private static ServerData<CChunkLoader> CHUNKLOADER_DATABASE;

    public static void initialize(){
        FCLForgeLibUtil.clearAllChunkLoaders();

        PlayerController.clearPDSections(FCLPlayerData.class);

        CHUNKLOADER_DATABASE = new ServerData<CChunkLoader>(){
            @Override
            public void onBlockMetaSet(BlockMetaData<CChunkLoader> blockMetaData) {

            }

            @Override
            public void onBlockMetaRemove(BlockMetaData<CChunkLoader> blockMetaData) {

            }
        };

        int playersWithChunkloader = 0;

        List<CChunkLoader> chunkloadersToLoadNow = new ArrayList<>();

        for (FCLPlayerData playerData : PlayerController.getAllPlayerData(FCLPlayerData.class)) {

            if (playerData.getChunkLoaders().size() > 0){
                playersWithChunkloader++;
            }

            boolean isPlayerOnline = playerData.isPlayerOnline();

            for (CChunkLoader chunkLoader : playerData.getChunkLoaders()) {

                BlockMetaData<CChunkLoader> existingChunkLoader = CHUNKLOADER_DATABASE.setBlockData(chunkLoader.getLocation(), chunkLoader);

                if (existingChunkLoader != null){
                    FinalChunkLoader.getLog().warning("Inconsistency on ChunkloaderBlocks, the same block has been placed by two different players at [" + chunkLoader.getLocation().toString() + "]:");
                    FinalChunkLoader.getLog().warning(" - " + existingChunkLoader.getValue().getOwner().getPlayerName());
                    FinalChunkLoader.getLog().warning(" - " + playerData.getPlayerName());
                }else if (isPlayerOnline || (chunkLoader.isAlwaysOn() && !chunkLoader.hasExpired())){
                    chunkloadersToLoadNow.add(chunkLoader);
                }

            }

        }

        List<CChunkLoader> allChunkLoaders = CHUNKLOADER_DATABASE.getAllBlockMetaData().stream()
                .map(BlockMetaData::getValue)
                .collect(Collectors.toList());;

        long allChunkLoadersSize = allChunkLoaders.size();

        List<CChunkLoader> alwaysOnChunkloaders = allChunkLoaders.stream().filter(CChunkLoader::isAlwaysOn).collect(Collectors.toList());
        List<CChunkLoader> onlineOnlyChunkloaders = allChunkLoaders.stream().filter(CChunkLoader::isOnlineOnly).collect(Collectors.toList());

        int toLoadNow = chunkloadersToLoadNow.size();

        final double PARTITION_LOAD_SIZE = 20D; //Chunks that will be loaded per 20ticks
        int parts = toLoadNow <= PARTITION_LOAD_SIZE
                ? 1
                : (int) (Math.floor(toLoadNow / PARTITION_LOAD_SIZE) + 1);

        List<List<CChunkLoader>> chunkloadersToLoadNowSubLists = FCCollectionsUtil.partitionEvenly(chunkloadersToLoadNow, parts);

        FinalChunkLoader.getLog().info(" Loaded [%s] chunk loaders data from (%s/%s) players!", allChunkLoadersSize, playersWithChunkloader, PlayerController.getAllPlayerData(FCLPlayerData.class).size());
        FinalChunkLoader.getLog().info("   AlwaysOn chunk loaders  : %s  (%s chunks)", alwaysOnChunkloaders.size(), alwaysOnChunkloaders.stream().mapToInt(CChunkLoader::totalChunks).sum());
        FinalChunkLoader.getLog().info("   OnlineOnly chunk loaders: %s  (%s chunks)", onlineOnlyChunkloaders.size(), onlineOnlyChunkloaders.stream().mapToInt(CChunkLoader::totalChunks).sum());

        if (toLoadNow == 0){
            FinalChunkLoader.getLog().info("There are no ChunkLoaders to be loaded now, skiping Loading Phase!");
            return;
        }

        FinalChunkLoader.getLog().info(" Marked %s chunk loaders (%s chunks) (alwaysOn=%s, onlineOnly=%s) to load NOW, in %s parts!",
                toLoadNow,
                chunkloadersToLoadNow.stream().mapToInt(CChunkLoader::totalChunks).sum(),
                alwaysOnChunkloaders.size(),
                (toLoadNow - alwaysOnChunkloaders.size()),
                parts
        );

        List<CChunkLoader> chunkloadersForRemovalCheck = new ArrayList<>();

        for (int i = 0; i < chunkloadersToLoadNowSubLists.size(); i++) {
            List<CChunkLoader> chunkLoaders = chunkloadersToLoadNowSubLists.get(i);

            //Load max of 5 chunks per 2 ticks
            FCScheduler.scheduleSyncInTicks(() -> {
                for (CChunkLoader chunkLoader : chunkLoaders) {
                    if (chunkLoader.validateChunkLoader()){
                        setChunkLoader(chunkLoader.getLocation(), chunkLoader);
                    }else {
                        chunkloadersForRemovalCheck.add(chunkLoader);
                    }
                }
            }, 2 * i);
        }

        //After all of them have been added, remove the ones that should not exist anymore
        FCScheduler.scheduleSyncInTicks(() -> {
            for (CChunkLoader chunkLoader : chunkloadersForRemovalCheck) {
                ChunkLoaderManager.removeChunkLoader(chunkLoader.getLocation());
            }
        }, (2 * chunkloadersToLoadNowSubLists.size()) + 100);

    }

    public static ServerData<CChunkLoader> getChunkloaderDatabase(){
        return CHUNKLOADER_DATABASE;
    }

    public static @Nullable CChunkLoader getChunkLoader(Location location){
        return CHUNKLOADER_DATABASE.getBlockData(location);
    }

    public static @Nullable CChunkLoader removeChunkLoader(Location location){
        CChunkLoader chunkLoader = Optional.ofNullable(CHUNKLOADER_DATABASE.setBlockData(location, null))
                .map(BlockMetaData::getValue)
                .orElse(null);

        if (chunkLoader != null){
            //Remove on PlayerData
            chunkLoader.getOwner().getChunkLoaders().remove(chunkLoader);
            chunkLoader.getOwner().setRecentChanged();

            //Remove on Forge
            FCLForgeLibUtil.removeChunkLoader(chunkLoader);
        }

        return chunkLoader == null ? null : chunkLoader;
    }

    public static @Nullable CChunkLoader setChunkLoader(Location location, CChunkLoader chunkLoader){
        BlockMetaData<CChunkLoader> prevData = CHUNKLOADER_DATABASE.setBlockData(location, chunkLoader);

        if (chunkLoader != null){
            if (!chunkLoader.getOwner().getChunkLoaders().contains(chunkLoader)) {
                chunkLoader.getOwner().getChunkLoaders().add(chunkLoader);
                chunkLoader.getOwner().setRecentChanged();
            }
            FCLForgeLibUtil.addChunkLoader(chunkLoader);
        }

        return prevData == null ? null : prevData.getValue();
    }

    public static List<CChunkLoader> getAllChunkLoaders(){
        return ChunkLoaderManager.getChunkloaderDatabase().getAllBlockMetaData().stream()
                .map(BlockMetaData::getValue)
                .collect(Collectors.toList());
    }

    public static List<CChunkLoader> getAllActivePremiumChunkLoaders(){
        return ChunkLoaderManager.getChunkloaderDatabase().getAllBlockMetaData().stream()
                .map(BlockMetaData::getValue)
                .filter(chunkLoader -> chunkLoader.isAlwaysOn() && !chunkLoader.hasExpired())
                .collect(Collectors.toList());
    }


}
