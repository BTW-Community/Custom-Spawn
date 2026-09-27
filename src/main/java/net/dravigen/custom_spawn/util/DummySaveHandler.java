package net.dravigen.custom_spawn.util;

import net.minecraft.src.*;

import java.io.File;

public class DummySaveHandler implements ISaveHandler {
	
	@Override
	public WorldInfo loadWorldInfo() {
		return null;
	}
	
	@Override
	public void checkSessionLock() {
	}
	
	@Override
	public IChunkLoader getChunkLoader(WorldProvider worldProvider) {
		return null;
	}
	
	@Override
	public void saveWorldInfoWithPlayer(WorldInfo worldInfo, NBTTagCompound nbtTagCompound) {
	}
	
	@Override
	public void saveWorldInfo(WorldInfo worldInfo) {
	}
	
	@Override
	public IPlayerFileData getSaveHandler() {
		return null;
	}
	
	@Override
	public void flush() {
	}
	
	@Override
	public File getMapFileFromName(String mapName) {
		return new File("dummy_map_file");
	}
	
	@Override
	public String getWorldDirectoryName() {
		return "DummyWorldDirectory";
	}
	
	@Override
	public void loadModSpecificData(WorldServer var1) {
	
	}
	
	@Override
	public void saveModSpecificData(WorldServer var1) {
	
	}
}