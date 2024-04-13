package br.com.finalcraft.finalchunkloader;

import br.com.finalcraft.evernifecore.ecplugin.annotations.ECPlugin;
import br.com.finalcraft.evernifecore.listeners.base.ECListener;
import br.com.finalcraft.evernifecore.logger.ECLogger;
import br.com.finalcraft.evernifecore.scheduler.FCScheduler;
import br.com.finalcraft.evernifecore.util.FCBukkitUtil;
import br.com.finalcraft.finalchunkloader.commands.CommandRegisterer;
import br.com.finalcraft.finalchunkloader.config.ConfigManager;
import br.com.finalcraft.finalchunkloader.integration.PlaceHolderIntegration;
import br.com.finalcraft.finalchunkloader.listeners.PlayerListener;
import br.com.finalcraft.finalchunkloader.util.FCLForgeLibUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@ECPlugin(
		bstatsID = "21497"
)
public class FinalChunkLoader extends JavaPlugin {

	public static FinalChunkLoader instance; { instance = this; } //Instance as early as possible!

	private final ECLogger ecLogger = new ECLogger(this);

	public static ECLogger getLog() {
		return instance.ecLogger;
	}

	@Override
	public void onEnable() {

		if (!FCBukkitUtil.isModLoaded("BCLForgeLib")){
			throw new RuntimeException("Crucible and BCLForgeLib are needed to run this plugin!");
		}

		getLog().info("Registering Commands...");
		CommandRegisterer.registerCommands(this);

		FCScheduler.scheduleSyncInTicks(() -> {
			//Load only after 2 seconds to make sure all other plugins are loaded and worlds as well
			getLog().info("Loading Configuration...");
			ConfigManager.initialize(this);

			getLog().info("Registering Listeners...");
			ECListener.register(this, PlayerListener.class);

			if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
				PlaceHolderIntegration.initialize(this);
			}
		},40);
	}

	@ECPlugin.Reload(
			reloadAfter = "EverNifeCore"
	)
	public void onReload(){
		ConfigManager.initialize(this);
	}

	@Override
	public void onDisable() {
		FCLForgeLibUtil.clearAllChunkLoaders();
	}

}
