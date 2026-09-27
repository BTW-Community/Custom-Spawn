package net.dravigen.custom_spawn.util;

import api.world.data.DataEntry;
import btw.world.BTWDifficulties;
import net.minecraft.src.*;

public class DummyWorld extends World {
	private final WorldChunkManager chunkManager;
	
	public DummyWorld(long seed, WorldType worldType) {
		super(new DummySaveHandler(),
			  "SeedSearcher",
			  WorldProviderSurface.getProviderForDimension(0),
			  new WorldSettings(seed, EnumGameType.SURVIVAL, true, false, worldType, BTWDifficulties.STANDARD, false),
			  null,
			  null);
		this.chunkManager = new WorldChunkManager(seed, worldType);
	}
	
	@Override
	public WorldChunkManager getWorldChunkManager() {
		return this.chunkManager;
	}
	
	@Override
	protected IChunkProvider createChunkProvider() {
		return null;
	}
	
	@Override
	public boolean spawnEntityInWorld(Entity par1Entity) {
		return false;
	}
	
	@Override
	public void calculateInitialSkylight() {
	}
	
	@Override
	public void tick() {
	}
	
	@Override
	public void updateAllLightTypes(int par1, int par2, int par3) {
	}
	
	@Override
	public Entity getEntityByID(int var1) {
		return null;
	}
	
	@Override
	public <T> T getData(DataEntry.WorldDataEntry<T> var1) {
		return null;
	}
	
	@Override
	public <T> void setData(DataEntry.WorldDataEntry<T> var1, T var2) {
	}
}
