package br.com.finalcraft.finalchunkloader.config.data;

import br.com.finalcraft.evernifecore.config.playerdata.PDSection;
import br.com.finalcraft.evernifecore.config.playerdata.PlayerData;
import br.com.finalcraft.evernifecore.config.yaml.section.ConfigSection;
import br.com.finalcraft.evernifecore.cooldown.Cooldown;
import br.com.finalcraft.evernifecore.minecraft.vector.BlockPos;
import br.com.finalcraft.finalchunkloader.FinalChunkLoader;
import br.com.finalcraft.finalchunkloader.chunkloader.CChunkLoader;
import br.com.finalcraft.finalchunkloader.config.rank.ChunksByGroup;
import br.com.finalcraft.finalchunkloader.config.settings.BCLSettings;
import br.com.finalcraft.finalchunkloader.config.settings.ChunkLoaderType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class FCLPlayerData extends PDSection {

	private List<CChunkLoader> chunkLoaders = new ArrayList<>();
	private int alwaysOnLimit;
	private int onlineOnlyLimit;

	public FCLPlayerData(PlayerData playerData) {
		super(playerData);

		this.alwaysOnLimit = getConfig().getInt("FinalChunkLoaders.alwaysOnLimit", 0);
		this.onlineOnlyLimit = getConfig().getInt("FinalChunkLoaders.onlineOnlyLimit", 0);

		ConfigSection chunkLoadersSection = getConfig().getConfigSection("FinalChunkLoaders.ChunkLoaders");
		if (chunkLoadersSection != null) {
			for (String worldWithPos : chunkLoadersSection.getKeys()) {
				try {
					ConfigSection configSection = chunkLoadersSection.getConfigSection(worldWithPos);
					String[] split = worldWithPos.split(" ");

					World world = Bukkit.getWorld(split[0]);
					if (world == null) {
						continue;
					}

					split = split[1].split("\\|");

					BlockPos blockPos = BlockPos.at(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));

					CChunkLoader chunkLoader = new CChunkLoader(
							this,
							blockPos.getLocation(world),
							(byte) configSection.getInt("range"),
							configSection.getBoolean("isAlwaysOn"),
							configSection.getBoolean("isAdminChunkLoader")
					);
					chunkLoaders.add(chunkLoader);
				}catch (Exception e){
					FinalChunkLoader.getLog().warning("Failed to load chunk loader at " + worldWithPos);
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void savePDSection() {
		getConfig().setValue("FinalChunkLoaders.alwaysOnLimit", alwaysOnLimit);
		getConfig().setValue("FinalChunkLoaders.onlineOnlyLimit", onlineOnlyLimit);

		getConfig().setValue("FinalChunkLoaders.ChunkLoaders", null);
		for (CChunkLoader chunkLoader : getChunkLoaders()) {
			ConfigSection configSection = getConfig().getConfigSection("FinalChunkLoaders.ChunkLoaders." + chunkLoader.getLocationString());
			configSection.setValue("location", chunkLoader.getLocation());
			configSection.setValue("range", chunkLoader.getRange());
			configSection.setValue("isAlwaysOn", chunkLoader.isAlwaysOn());
			configSection.setValue("isAdminChunkLoader", chunkLoader.isAdminChunkLoader());
		}
	}

	private Cooldown PERM_CHECK_COOLDOWN = new Cooldown.GenericCooldown("");

	public void recalculateGroupLimitsIfNecessary(){
		if (!PERM_CHECK_COOLDOWN.isInCooldown()){
			PERM_CHECK_COOLDOWN.startWith(10, TimeUnit.SECONDS);
			ChunksByGroup.recalibratePlayerLimits(this);
		}
	}

	private void recalibraLimits(){
		this.alwaysOnLimit = Math.min(this.alwaysOnLimit, BCLSettings.MAX_CHUNK_AMOUNT_ALWAYSON);
		this.onlineOnlyLimit = Math.min(this.onlineOnlyLimit, BCLSettings.MAX_CHUNK_AMOUNT_ONLINEONLY);
	}

	public List<CChunkLoader> getChunkLoaders() {
		return chunkLoaders;
	}

	public int getChunksLimit(ChunkLoaderType chunkLoaderType){
		return chunkLoaderType == ChunkLoaderType.ALWAYS_ON ? alwaysOnLimit : onlineOnlyLimit;
	}

	public int getChunksLimitRemaining(ChunkLoaderType chunkLoaderType){
		int limit = getChunksLimit(chunkLoaderType);
		int used = getChunkLoaders().stream()
				.filter(cl -> cl.getChunkLoaderType() == chunkLoaderType)
				.mapToInt(CChunkLoader::totalChunks)
				.sum();

		return limit - used;
	}

	public void setChunksLimit(ChunkLoaderType chunkLoaderType, int limit){
		if (chunkLoaderType == ChunkLoaderType.ALWAYS_ON) {
			this.alwaysOnLimit = limit;
		} else {
			this.onlineOnlyLimit = limit;
		}
		recalibraLimits();
		setRecentChanged();
	}

	public long getChunkLoadersInUseCount(@Nullable ChunkLoaderType chunkLoaderType){
		return getChunkLoaders().stream()
				.filter(cChunkLoader -> chunkLoaderType == null || cChunkLoader.getChunkLoaderType() == chunkLoaderType)
				.count();
	}

}
