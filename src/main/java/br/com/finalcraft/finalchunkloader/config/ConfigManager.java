package br.com.finalcraft.finalchunkloader.config;

import br.com.finalcraft.evernifecore.config.Config;
import br.com.finalcraft.evernifecore.locale.FCLocaleManager;
import br.com.finalcraft.finalchunkloader.config.datastore.ChunkLoaderManager;
import br.com.finalcraft.finalchunkloader.config.rank.ChunksByGroup;
import br.com.finalcraft.finalchunkloader.config.settings.FCLSettings;
import br.com.finalcraft.finalchunkloader.guis.LayoutManager;
import br.com.finalcraft.finalchunkloader.messages.FCLMessages;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private static Config config;
    private static Config chunksByRank;

    public static Config getMainConfig() {
        return config;
    }

    public static Config getChunksByRank(){
        return chunksByRank;
    }

    public static void initialize(JavaPlugin instance){
        config          = new Config(instance,"config.yml",true);
        chunksByRank    = new Config(instance,"ChunksByGroup.yml", true);

        FCLSettings.initialize();
        LayoutManager.initialize();
        ChunksByGroup.initialize();
        ChunkLoaderManager.initialize();

        FCLocaleManager.loadLocale(
                instance,
                FCLMessages.class
        );
    }
}
