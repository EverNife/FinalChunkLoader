package br.com.finalcraft.finalchunkloader.listeners;

import br.com.finalcraft.evernifecore.api.events.ECFullyLoggedInEvent;
import br.com.finalcraft.evernifecore.config.playerdata.PlayerController;
import br.com.finalcraft.evernifecore.listeners.base.ECListener;
import br.com.finalcraft.evernifecore.locale.FCLocale;
import br.com.finalcraft.evernifecore.locale.LocaleMessage;
import br.com.finalcraft.evernifecore.locale.LocaleType;
import br.com.finalcraft.evernifecore.minecraft.worlddata.BlockMetaData;
import br.com.finalcraft.evernifecore.minecraft.worlddata.WorldData;
import br.com.finalcraft.evernifecore.scheduler.FCScheduler;
import br.com.finalcraft.evernifecore.util.FCBukkitUtil;
import br.com.finalcraft.evernifecore.util.FCMessageUtil;
import br.com.finalcraft.finalchunkloader.FinalChunkLoader;
import br.com.finalcraft.finalchunkloader.PermissionNodes;
import br.com.finalcraft.finalchunkloader.chunkloader.CChunkLoader;
import br.com.finalcraft.finalchunkloader.config.data.FCLPlayerData;
import br.com.finalcraft.finalchunkloader.config.datastore.ChunkLoaderManager;
import br.com.finalcraft.finalchunkloader.config.rank.ChunksByGroup;
import br.com.finalcraft.finalchunkloader.config.settings.BCLSettings;
import br.com.finalcraft.finalchunkloader.config.settings.ChunkLoaderType;
import br.com.finalcraft.finalchunkloader.guis.gui.ChunkLoaderEditGui;
import br.com.finalcraft.finalchunkloader.listeners.util.ChunkLoaderVisualizationUtil;
import br.com.finalcraft.finalchunkloader.messages.FCLMessages;
import br.com.finalcraft.finalchunkloader.util.FCLForgeLibUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerListener implements ECListener {

	@FCLocale(lang = LocaleType.PT_BR, text = "§4§l ▶ §cVocê não pode editar o ChunkLoader de outros jogadores!")
	@FCLocale(lang = LocaleType.EN_US, text = "§4§l ▶ §cYou cannot edit other's ChunkLoaders!")
	private static LocaleMessage CANNOT_EDIT_OTHERS_CHUNKLOADERS;

	@FCLocale(lang = LocaleType.EN_US, text = "§a§m-----------------§r[ §6ChunkLoader Info ]§a§m------------------§r" +
			"\n" +
			"\n&6&l ▶ &e&lDono: &b&l%owner%" +
			"\n&6&l ▶ &e&lBlockPos: &a%location%" +
			"\n&6&l ▶ &e&lChunkPos: &a%world% %chunkX%,%chunkZ%" +
			"\n&6&l ▶ &e&lSize: &a%size%" +
			"\n" +
			"\n§a§m-----------------------------------------------------")
	private static LocaleMessage CHUNK_LOADER_INFO;

	@FCLocale(lang = LocaleType.PT_BR, text = "§4§l ▶ §eO seu ChunkLoader em [%location%] foi removido por §b%player%!")
	@FCLocale(lang = LocaleType.EN_US, text = "§4§l ▶ §eYour ChunkLoader at [%location%] has been removed by §b%player%!")
	private static LocaleMessage YOUR_CHUNK_LOADER_HAS_BEEN_REMOVED_BY;

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

	    Player player = event.getPlayer();
		Block clickedBlock = event.getClickedBlock();
		
		if (clickedBlock == null || player == null) {
			return;
		}

		if (!BCLSettings.isWorldEnabled(player.getWorld().getName())) {
			return;
		}

		if (FCBukkitUtil.isFakePlayer(player)){
			return;
		}

		//Check if the block is a possible chunkloader
		ChunkLoaderType chunkLoaderType = BCLSettings.getChunkLoaderType(clickedBlock);

		if (chunkLoaderType == null) {
			return;
		}

		ItemStack itemStack = FCBukkitUtil.getPlayersHeldItem(player);

		CChunkLoader chunkLoader = ChunkLoaderManager.getChunkLoader(clickedBlock.getLocation());
		boolean isHoldingTheEditRod = itemStack != null && BCLSettings.INTERACTION_ROD.match(itemStack) == true;

		if (chunkLoader != null){

			if (isHoldingTheEditRod == false){
				//If not holding the ROD, show details!
				CHUNK_LOADER_INFO.
						addPlaceholder("%owner%", chunkLoader.getOwnerName()).
						addPlaceholder("%location%", chunkLoader.getLocationString()).
						addPlaceholder("%world%", chunkLoader.getWorldName()).
						addPlaceholder("%chunkX%", chunkLoader.getChunkX()).
						addPlaceholder("%chunkZ%", chunkLoader.getChunkZ()).
						addPlaceholder("%size%", chunkLoader.sizeInString()).
						send(player);

				if (player.isSneaking()) {
					FCScheduler.runAssync(() -> {
						ChunkLoaderVisualizationUtil.showCorners(chunkLoader, player);
					});
				}

				return;
			}

			//If holding the hod, show the UI
			if (player.getUniqueId().equals(chunkLoader.getOwner().getUniqueId())
					|| player.hasPermission(PermissionNodes.ADMIN_EDIT_ALL)
					|| (chunkLoader.isAdminChunkLoader() && player.hasPermission(PermissionNodes.ADMIN_LOADER))) {

				FCLPlayerData viewerData = PlayerController.getPDSection(player, FCLPlayerData.class);

				new ChunkLoaderEditGui(viewerData,chunkLoader).open();
			} else {
				CANNOT_EDIT_OTHERS_CHUNKLOADERS.send(player);
			}

			return;
		}

		if (isHoldingTheEditRod == false){
			return; //Even being a chunkloader_block, if not holding the rod, do nothing
		}

		if (!canBreak(clickedBlock, player)){ //Check if we can break here
			FCMessageUtil.needsThePermission(player);
			return;
		}

		boolean isAdminChunkLoader = false;

		if (chunkLoaderType == ChunkLoaderType.ALWAYS_ON) {
			if (!player.hasPermission(PermissionNodes.CHUNK_ALWAYSON)) {
				FCMessageUtil.needsThePermission(player, PermissionNodes.CHUNK_ALWAYSON);
				return;
			}
			if (player.isSneaking() && player.hasPermission(PermissionNodes.ADMIN_LOADER)) {
				isAdminChunkLoader = true;
			}
		} else {
			if (!player.hasPermission(PermissionNodes.CHUNK_ONLINEONLY)) {
				FCMessageUtil.needsThePermission(player, PermissionNodes.CHUNK_ONLINEONLY);
				return;
			}
		}

		FCLPlayerData playerData = PlayerController.getPDSection(player, FCLPlayerData.class);

		playerData.recalculateGroupLimitsIfNecessary();

		boolean hasAtLeastOneChunk = playerData.getChunksLimitRemaining(chunkLoaderType) >= 1;

		CChunkLoader newChunkLoader = new CChunkLoader(
				playerData,
				clickedBlock.getLocation(),
				 hasAtLeastOneChunk ? (byte) 0 : (byte)-1,
				chunkLoaderType == ChunkLoaderType.ALWAYS_ON,
				isAdminChunkLoader
		);

		if (hasAtLeastOneChunk){
			ChunkLoaderManager.setChunkLoader(clickedBlock.getLocation(), newChunkLoader);
			FCLMessages.CHUNK_LOADER_SUCCESSFULLY_CREATED.send(player);
		}

		new ChunkLoaderEditGui(playerData,newChunkLoader).open();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (block == null) {
			return;
		}

		if (!BCLSettings.isWorldEnabled(block.getLocation().getWorld().getName())) {
			return;
		}

		//Check if the block is a possible chunkloader
		ChunkLoaderType chunkLoaderType = BCLSettings.getChunkLoaderType(block);
		if (chunkLoaderType == null) {
			return;
		}

		CChunkLoader chunkLoader = ChunkLoaderManager.removeChunkLoader(block.getLocation());
		if (chunkLoader == null) {
			return;//No chunkloader removed
		}

		FCLMessages.CHUNK_LOADER_SUCCESSFULLY_REMOVED.send(player);

		if (!chunkLoader.isAdminChunkLoader() && !player.getUniqueId().equals(chunkLoader.getOwner().getUniqueId()) && chunkLoader.getOwner().isPlayerOnline()) {
			YOUR_CHUNK_LOADER_HAS_BEEN_REMOVED_BY.
					addPlaceholder("%location%", chunkLoader.getLocationString()).
					addPlaceholder("%player%", player.getDisplayName()).
					send(chunkLoader.getOwner().getPlayer());
		}

		FinalChunkLoader.getLog().info(
				"[%s] broke [%s]'s [%s::ChunkLoader] at [%s]",
				player.getName(),
				chunkLoader.getOwnerName(),
				chunkLoader.getChunkLoaderType(),
				chunkLoader.getLocationString()
		);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onPlayerLogin(ECFullyLoggedInEvent event) {

		FCScheduler.scheduleSyncInTicks(() -> {
			if (event.getPlayer().isOnline()){
				FCLPlayerData playerData = event.getPDSection(FCLPlayerData.class);

				ChunksByGroup.recalibratePlayerLimits(playerData);

				for (CChunkLoader chunkLoader : playerData.getChunkLoaders()) {
					//Turn his chunk loaders 'onlineOnly' on
					if (chunkLoader.isOnlineOnly()) {
						if (chunkLoader.validateChunkLoader() == false){
							ChunkLoaderManager.removeChunkLoader(chunkLoader.getLocation());
						}
						FCLForgeLibUtil.addChunkLoader(chunkLoader);
					}
				}

			}
		}, 20);

	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	void onPlayerQuit(PlayerQuitEvent event) {
		FCLPlayerData playerData = PlayerController.getPDSection(event.getPlayer(), FCLPlayerData.class);

		List<CChunkLoader> clList = playerData.getChunkLoaders();

		for (CChunkLoader chunkLoader : clList) {
			if (chunkLoader.isOnlineOnly()) {
				FCLForgeLibUtil.removeChunkLoader(chunkLoader);
			}
		}
	}
	
    @EventHandler(priority = EventPriority.MONITOR)
    void onWorldLoad(WorldLoadEvent event) {
		WorldData<CChunkLoader> worldData = ChunkLoaderManager.getChunkloaderDatabase().getWorldData(event.getWorld().getName());

		if (worldData != null){
			worldData.getAllBlockData().stream()
					.map(BlockMetaData::getValue)
					.filter(CChunkLoader::isLoadable)
					.forEach(chunkLoader -> {
						FCLForgeLibUtil.addChunkLoader(chunkLoader);
					});
		}
    }

	private static boolean canBreak(Block block, Player player) {
		BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);
		Bukkit.getServer().getPluginManager().callEvent(breakEvent);
		return !breakEvent.isCancelled();
	}

}
