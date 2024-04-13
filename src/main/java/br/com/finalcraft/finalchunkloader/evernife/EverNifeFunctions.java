package br.com.finalcraft.finalchunkloader.evernife;

import br.com.finalcraft.finalchunkloader.chunkloader.CChunkLoader;
import br.com.finalcraft.finalchunkloader.config.data.FCLPlayerData;
import br.com.finalcraft.finalchunkloader.config.datastore.ChunkLoaderManager;

import java.util.ArrayList;
import java.util.List;

public class EverNifeFunctions {

    public static List<CChunkLoader> getActivePremiumChunksFromPlayer(FCLPlayerData playerData){
        List<CChunkLoader> chunkLoaders = new ArrayList<CChunkLoader>();

        for (CChunkLoader chunkLoader : playerData.getChunkLoaders()) {
            if (chunkLoader.isAlwaysOn()) {
                chunkLoaders.add(chunkLoader);
            }
        }

        return chunkLoaders;
    }

}
