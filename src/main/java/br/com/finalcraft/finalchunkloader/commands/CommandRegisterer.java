package br.com.finalcraft.finalchunkloader.commands;

import br.com.finalcraft.evernifecore.commands.finalcmd.FinalCMDManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandRegisterer {

    public static void registerCommands(JavaPlugin pluginInstance) {

        FinalCMDManager.registerCommand(pluginInstance, CMDChunkLoader.class);

    }

}
