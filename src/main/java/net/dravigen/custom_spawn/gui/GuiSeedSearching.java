package net.dravigen.custom_spawn.gui;

import api.world.difficulty.DifficultyParam;
import btw.world.BTWDifficulties;
import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.dravigen.custom_spawn.config.ConfigUtils;
import net.dravigen.custom_spawn.util.SearchController;
import net.minecraft.src.*;

import static net.dravigen.custom_spawn.CustomSpawnAddon.*;

public class GuiSeedSearching extends GuiScreen {
	private final SearchController controller;
	private final String textboxWorldName;
	private final String folderName;
	private final String gameMode;
	private final int difficultyID;
	private final boolean generateStructures;
	private final boolean commandsAllowed;
	private final boolean bonusItems;
	private final boolean isHardcore;
	public int worldTypeId;
	public String generatorOptionsToUse;
	private boolean lockDifficulty;
	
	public GuiSeedSearching(long startSeed, String gameMode, int difficultyID, boolean lockDifficulty,
			boolean generateStructures, boolean isHardcore, int worldTypeId, String generatorOptionsToUse,
			String folderName, String textboxWorldName, boolean bonusItems, boolean commandsAllowed) {
		this.gameMode = gameMode;
		this.difficultyID = difficultyID;
		this.lockDifficulty = lockDifficulty;
		this.generateStructures = generateStructures;
		this.commandsAllowed = commandsAllowed;
		this.bonusItems = bonusItems;
		this.isHardcore = isHardcore;
		this.worldTypeId = worldTypeId;
		this.folderName = folderName;
		this.textboxWorldName = textboxWorldName;
		this.generatorOptionsToUse = generatorOptionsToUse;
		
		this.controller = new SearchController(startSeed, worldTypeId);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.buttonList.clear();
		
		String status = "";
		if (controller.isSearching()) {
			status = I18n.getStringParams("customspawn.loading.attempts", controller.getAttempts());
		}
		else {
			this.getSeedSpawnBiomes(new WorldChunkManager(controller.getFoundSeed(),
														  WorldType.worldTypes[this.worldTypeId]),
									controller.getFoundChunk());
			this.createWorld(controller.getFoundSeed());
			
			CustomSpawnAddon.isSearching = false;
			this.mc.displayGuiScreen(null);
		}
		
		this.drawCenteredString(this.fontRenderer, status, this.width / 2, this.height / 2, 0xFFFFFF);
		
		GuiButton done = new GuiButton(200,
									   this.width / 2 - 100,
									   this.height - 25,
									   I18n.getString("customspawn.loading.abort"));
		this.buttonList.add(done);
		done.drawButton(this.mc, mouseX, mouseY);
		
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button.id == 200) {
				this.controller.stopSearch();
				this.mc.displayGuiScreen(new GuiCreateWorld(null));
			}
		}
	}
	
	@Override
	public void initGui() {
		controller.startSearch();
		this.buttonList.clear();
	}
	
	@Override
	public void onGuiClosed() {
		if (controller.isSearching()) {
			controller.stopSearch();
		}
		
		CustomSpawnAddon.isSearching = false;
	}
	
	public void getSeedSpawnBiomes(WorldChunkManager manager, ChunkPosition chunk) {
		IntCache.resetIntCache();
		allBiomeFound.clear();
		wantedBiomesFound.clear();
		unwantedBiomesFound.clear();
		
		final int SPAWN_RADIUS = (17 * 16) / 2;
		
		GenLayer genBiomes = manager.genBiomes;
		
		if (chunk == null) return;
		
		int centerX = chunk.x;
		int centerZ = chunk.z;
		int centerBiomeX = centerX >> 2;
		int centerBiomeZ = centerZ >> 2;
		
		int centerBiomeInt = genBiomes.getInts(centerBiomeX, centerBiomeZ, 1, 1)[0];
		BiomeGenBase centerBiome = BiomeGenBase.biomeList[centerBiomeInt];
		String centerName = centerBiome.biomeName.replace(" ", "");
		
		if (!allBiomesName.contains(onlyBiome)) {
			if (!ConfigUtils.getBoolean(ConfigUtils.suitableBiomesKey + "." + centerName)) return;
		}
		else if (!onlyBiome.equalsIgnoreCase(centerName)) return;
		
		int minBiomeX = centerX - SPAWN_RADIUS >> 2;
		int minBiomeZ = centerZ - SPAWN_RADIUS >> 2;
		
		int maxBiomeX = centerX + SPAWN_RADIUS >> 2;
		int maxBiomeZ = centerZ + SPAWN_RADIUS >> 2;
		
		int mapWidth = maxBiomeX - minBiomeX + 1;
		int mapHeight = maxBiomeZ - minBiomeZ + 1;
		
		int[] biomeInts = genBiomes.getInts(minBiomeX, minBiomeZ, mapWidth, mapHeight);
		
		for (int mapIndex = 0; mapIndex < mapWidth * mapHeight; ++mapIndex) {
			String currentBiome = BiomeGenBase.biomeList[biomeInts[mapIndex]].biomeName.replace(" ", "");
			
			if (wantedBiomesInSpawn.contains(currentBiome)) {
				wantedBiomesFound.add(currentBiome);
			}
			else if (unwantedBiomesInSpawn.contains(currentBiome)) {
				unwantedBiomesFound.add(currentBiome);
			}
			else {
				allBiomeFound.add(currentBiome);
			}
		}
	}
	
	private void createWorld(long seed) {
		EnumGameType gameType = EnumGameType.getByName(gameMode);
		if (!BTWDifficulties.DIFFICULTY_LIST.get(difficultyID)
				.getParamValue(DifficultyParam.CanDifficultyBeChanged.class)) {
			lockDifficulty = true;
		}
		WorldSettings settings = new WorldSettings(seed,
												   gameType,
												   generateStructures,
												   isHardcore,
												   WorldType.worldTypes[worldTypeId],
												   BTWDifficulties.DIFFICULTY_LIST.get(difficultyID),
												   lockDifficulty);
		settings.func_82750_a(generatorOptionsToUse);
		if (bonusItems && !isHardcore) {
			settings.enableBonusChest();
		}
		if (commandsAllowed && !isHardcore) {
			settings.enableCommands();
		}
		Minecraft.getMinecraft().launchIntegratedServer(folderName, textboxWorldName, settings);
		
		Minecraft.getMinecraft().statFileWriter.readStat(StatList.createWorldStat, 1);
	}
	
}
