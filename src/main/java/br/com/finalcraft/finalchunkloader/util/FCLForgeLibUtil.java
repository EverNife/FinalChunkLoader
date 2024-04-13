package br.com.finalcraft.finalchunkloader.util;

import net.kaikk.mc.bcl.forgelib.BCLForgeLib;
import net.kaikk.mc.bcl.forgelib.ChunkLoader;

import java.util.List;
import java.util.stream.Collectors;

public class FCLForgeLibUtil {

    public static void addChunkLoader(ChunkLoader chunkLoader){
        BCLForgeLib.instance().addChunkLoader(chunkLoader);
    }

    public static void removeChunkLoader(ChunkLoader chunkLoader){
        BCLForgeLib.instance().removeChunkLoader(chunkLoader);
    }

    public static void clearAllChunkLoaders(){
        List<ChunkLoader> allChunkLoaders = BCLForgeLib.instance().getChunkLoaders().values().stream()
                .flatMap(chunkLoaders -> chunkLoaders.stream())
                .collect(Collectors.toList());

        for (ChunkLoader chunkLoader : allChunkLoaders) {
            BCLForgeLib.instance().removeChunkLoader(chunkLoader);
        }
    }

}
