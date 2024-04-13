package br.com.finalcraft.finalchunkloader.chunkloader;

import br.com.finalcraft.evernifecore.minecraft.vector.BlockPos;
import br.com.finalcraft.evernifecore.minecraft.vector.ChunkPos;
import br.com.finalcraft.finalchunkloader.config.data.FCLPlayerData;
import br.com.finalcraft.finalchunkloader.config.settings.BCLSettings;
import br.com.finalcraft.finalchunkloader.config.settings.ChunkLoaderType;
import net.kaikk.mc.bcl.forgelib.ChunkLoader;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.concurrent.TimeUnit;

public class CChunkLoader extends ChunkLoader {

	private final FCLPlayerData owner;
	private final Location location;
	private final ChunkPos chunkPos;
	private final BlockPos blockPos;
	private final boolean isAlwaysOn;
	private final boolean isAdminChunkLoader;

	public boolean markDisabled = false;

	public CChunkLoader(FCLPlayerData owner, Location location, byte range, boolean isAlwaysOn, boolean isAdminChunkLoader) {
		this.owner = owner;
		this.location = location;
		this.chunkPos = ChunkPos.from(location);
		this.blockPos = BlockPos.from(location);
		this.isAlwaysOn = isAlwaysOn;
		this.isAdminChunkLoader = isAdminChunkLoader;

		this.chunkX = this.chunkPos.getX();
		this.chunkZ = this.chunkPos.getZ();
		this.worldName = location.getWorld().getName();
		this.range = range;
	}

	public Location getLocation() {
		return location;
	}

	public World getWorld() {
		return location.getWorld();
	}

	public boolean hasExpired() {
		return this.isAdminChunkLoader() ? false : System.currentTimeMillis() - owner.getLastSeen() > TimeUnit.HOURS.toMillis(BCLSettings.MAX_HOURS_OFFLINE);
	}

	public String getOwnerName() {
		return owner.getPlayerName();
	}

	public int side() {
		if (getRange() < 0) return 0;
		return 1 + (super.getRange() * 2);
	}

	public int totalChunks() {
		return this.side() * this.side();
	}

	public String sizeInString() {
		return this.side()+"x"+this.side();
	}

	public boolean isLoadable() {
		return (this.isOwnerOnline() || (this.isAlwaysOn && !this.hasExpired())) && this.validateChunkLoader();
	}

	public boolean validateChunkLoader() {
		Block chunkLoaderBlock = this.location.getBlock();

		if (chunkLoaderBlock == null) {
			return false;
		}

		if (isAlwaysOn) {
			return BCLSettings.ALWAYS_ON_BLOCK.match(chunkLoaderBlock);
		} else {
			return BCLSettings.ONLINE_ONLY_BLOCK.match(chunkLoaderBlock);
		}
	}

	public boolean isOwnerOnline() {
		return this.owner.isPlayerOnline();
	}

	@Override
	public String toString() {
		return (this.isAlwaysOn ? "y" : "n") + " - " + this.sizeInString() + " - " + this.getLocationString();
	}

	public FCLPlayerData getOwner() {
		return owner;
	}

	public BlockPos getLoc() {
		return blockPos;
	}

	public String getLocationString() {
		return worldName + " " + blockPos.toString();
	}

	public boolean isAlwaysOn() {
		return isAlwaysOn;
	}

	public boolean isOnlineOnly() {
		return !isAlwaysOn;
	}

	@Override
	public byte getRange() { //[-1,0,1,2,3,4...]
		return super.range;
	}

	public void setRange(int range) {
		super.range = (byte) range;
	}

	public boolean isAdminChunkLoader() {
		return isAdminChunkLoader;
	}

	public ChunkLoaderType getChunkLoaderType() {
		return this.isAlwaysOn ? ChunkLoaderType.ALWAYS_ON : ChunkLoaderType.ONLINE_ONLY;
	}

}
