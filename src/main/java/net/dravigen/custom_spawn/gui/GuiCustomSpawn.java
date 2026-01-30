package net.dravigen.custom_spawn.gui;

import net.dravigen.custom_spawn.CustomSpawnAddon;
import net.dravigen.custom_spawn.config.BaseSetting;
import net.dravigen.custom_spawn.config.ConfigUpdater;
import net.dravigen.custom_spawn.config.ConfigUtils;
import net.dravigen.custom_spawn.util.LocalizationUtil;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GuiCustomSpawn extends GuiScreen {
	public static final int buttonIDStart = 1000;
	private static final int HEADER_HEIGHT = 30;
	private static final int ITEM_HEIGHT = 22;
	private static final int BUTTON_WIDTH = 100;
	private static final int BUTTON_HEIGHT = 20;
	private static final int SCROLLBAR_WIDTH = 6;
	private static final int SCROLL_AREA_PADDING = 8;
	private static final int SECTION_HEADER_HEIGHT = 16;
	private static boolean saved = false;
	private static long timeSinceSaved = 0;
	private final GuiScreen parentScreen;
	private final TreeMap<String, List<BaseSetting>> categorizedSettings;
	private final List<BaseSetting> settingList;
	
	private float scrollOffset = 0.0F;
	private boolean isScrolling = false;
	
	public GuiCustomSpawn(GuiScreen parent) {
		this.parentScreen = parent;
		this.settingList = ConfigUtils.getSettings();
		
		this.categorizedSettings = new TreeMap<>();
		
		for (BaseSetting setting : this.settingList) {
			String category = setting.category() == null || setting.category().isEmpty()
							  ? "0.General"
							  : setting.category();
			this.categorizedSettings.computeIfAbsent(I18n.getString(category), k -> new ArrayList<>()).add(setting);
		}
	}
	
	private void handleSettingInteraction(int buttonId) {
		int settingIndex = MathHelper.floor_double((buttonId - buttonIDStart) / 20d);
		int actionType = (buttonId - buttonIDStart) % 20;
		
		if (settingIndex >= 0 && settingIndex < settingList.size()) {
			BaseSetting setting = settingList.get(settingIndex);
			boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
			
			if (setting.type() == ConfigUtils.Type.BOOLEAN) {
				boolean currentValue = ConfigUtils.getBoolean(setting.id());
				ConfigUtils.setValue(setting.id(), !currentValue);
			}
			else if (setting.type() == ConfigUtils.Type.INT) {
				int currentValue = ConfigUtils.getInt(setting.id());
				
				int delta = (actionType == 0) ? -1 : 1;
				
				int newValue = shift
							   ? (actionType == 0) ? currentValue / 2 : currentValue * 2
							   : (int) (currentValue + delta * (setting.max() / 16));
				
				newValue -= (int) (newValue % (setting.max() / 16));
				
				newValue = (int) Math.max(newValue, setting.min());
				newValue = (int) Math.min(newValue, setting.max());
				
				ConfigUtils.setValue(setting.id(), newValue);
			}
			else if (setting.type() == ConfigUtils.Type.STRING) {
				String currentValue = ConfigUtils.getString(setting.id());
				
				int index = CustomSpawnAddon.allBiomesName.size();
				
				List<String> allBiomes = CustomSpawnAddon.allBiomesName;
				
				for (String b : allBiomes) {
					if (b.equalsIgnoreCase(currentValue)) {
						index = allBiomes.indexOf(b);
						
						break;
					}
				}
				
				int delta = (actionType == 0) ? -1 : 1;
				
				index += delta;
				
				index = index < 0 ? allBiomes.size() : index > allBiomes.size() ? 0 : index;
				
				String newValue = index == allBiomes.size() ? "none" : allBiomes.get(index);
				
				ConfigUtils.setValue(setting.id(), newValue);
			}
			else if (setting.type() == ConfigUtils.Type.MUL_STRING) {
				String currentValue = ConfigUtils.getString(setting.id()).split(",")[0];
				
				List<String> otherValues = new ArrayList<>(List.of(ConfigUtils.getString(setting.id()).split(",")));
				
				otherValues.remove(currentValue);
				StringBuilder string = new StringBuilder();
				
				if (actionType == 2) {
					string.append("none,");
					string.append(currentValue);
					
					for (String s : otherValues) {
						string.append(",").append(s);
					}
					
					ConfigUtils.setValue(setting.id(), string.toString());
					
					return;
				}
				
				if (actionType >= 3) {
					int i = actionType - 3;
					
					otherValues.remove(i);
					
					string.append(currentValue);
					
					for (String s : otherValues) {
						string.append(",").append(s);
					}
					
					ConfigUtils.setValue(setting.id(), string.toString());
					
					return;
				}
				
				int index = CustomSpawnAddon.allBiomesName.size();
				
				List<String> allBiomes = CustomSpawnAddon.allBiomesName;
				
				for (String b : allBiomes) {
					if (b.equalsIgnoreCase(currentValue)) {
						index = allBiomes.indexOf(b);
						
						break;
					}
				}
				
				String newValue;
				
				List<String> uwBiomes = new ArrayList<>(CustomSpawnAddon.unwantedBiomesInSpawn);
				List<String> wBiomes = new ArrayList<>(CustomSpawnAddon.wantedBiomesInSpawn);
				
				if (!wBiomes.isEmpty()) {
					wBiomes.remove(0);
				}
				if (!uwBiomes.isEmpty()) {
					uwBiomes.remove(0);
				}
				
				do {
					int delta = (actionType == 0) ? -1 : actionType == 1 ? 1 : 0;
					
					index += delta;
					
					index = index < 0 ? allBiomes.size() : index > allBiomes.size() ? 0 : index;
					
					newValue = index == allBiomes.size() ? "none" : allBiomes.get(index);
					
					if (newValue.equalsIgnoreCase("none")) break;
				} while (otherValues.contains(newValue) ||
						(setting.id().equalsIgnoreCase(ConfigUtils.wantedBiomesInSpawnKey) &&
								uwBiomes.contains(newValue)) ||
						(setting.id().equalsIgnoreCase(ConfigUtils.unwantedBiomesInSpawnKey) &&
								wBiomes.contains(newValue)));
				
				string.append(newValue);
				
				for (String s : otherValues) {
					string.append(",").append(s);
				}
				
				ConfigUtils.setValue(setting.id(), string.toString());
			}
		}
	}
	
	private void applyClipping(int x, int y, int width, int height) {
		if (width <= 0 || height <= 0) return;
		
		ScaledResolution sr = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
		int scale = sr.getScaleFactor();
		
		int nativeX = x * scale;
		int nativeY = this.mc.displayHeight - (y + height) * scale;
		int nativeWidth = width * scale;
		int nativeHeight = height * scale;
		
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(nativeX, nativeY, nativeWidth, nativeHeight);
	}
	
	private void clearClipping() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		String hoveredDescription = null;
		
		this.drawCenteredString(this.fontRenderer, I18n.getString("customspawn.title"), this.width / 2, 10, 0xFFFFFF);
		
		int scrollXStart = (int) (this.width / 2d - Math.max(150, this.width / 3.5));
		int scrollXEnd = (int) (this.width / 2d + Math.max(150, this.width / 3.5));
		
		int scrollYStart = HEADER_HEIGHT;
		int scrollYEnd = this.height - (BUTTON_HEIGHT) * 3 - 1;
		int scrollHeight = scrollYEnd - scrollYStart;
		
		int numCategories = categorizedSettings.size();
		
		int contentHeight = getContentHeight(numCategories);
		
		float maxScroll = Math.max(0, contentHeight - scrollHeight);
		int currentScrollY = (int) (scrollOffset * maxScroll);
		
		// 1. Draw list background
		drawRect(0, scrollYStart - SCROLL_AREA_PADDING, this.width, scrollYEnd + SCROLL_AREA_PADDING, 0xAA000000);
		/*drawRect(scrollXStart - SCROLL_AREA_PADDING,
				 scrollYStart - SCROLL_AREA_PADDING,
				 scrollXEnd + SCROLLBAR_WIDTH + SCROLL_AREA_PADDING,
				 scrollYEnd + SCROLL_AREA_PADDING,
				 0xAA000000);*/
		int itemY = scrollYStart - currentScrollY;
		
		this.buttonList.clear();
		
		// 2. Clip
		this.applyClipping(scrollXStart,
						   scrollYStart,
						   scrollXEnd - scrollXStart + SCROLLBAR_WIDTH,
						   scrollYEnd - scrollYStart);
		
		// 3. Render Items
		{
			for (Map.Entry<String, List<BaseSetting>> categoryEntry : categorizedSettings.entrySet()) {
				String categoryName = categoryEntry.getKey();
				List<BaseSetting> settings = categoryEntry.getValue();
				
				// Draw Category Header
				if (itemY + SECTION_HEADER_HEIGHT > scrollYStart && itemY < scrollYEnd) {
					this.drawCenteredString(this.fontRenderer,
											categoryName.split("\\.")[1],
											scrollXStart + (scrollXEnd - scrollXStart) / 2,
											itemY + 4,
											0xFFAA00);
				}
				
				itemY += SECTION_HEADER_HEIGHT;
				
				for (BaseSetting setting : settings) {
					int originalIndex = settingList.indexOf(setting);
					int itemMouseYStart = Math.max(scrollYStart, itemY);
					int itemMouseYEnd = Math.min(scrollYEnd, itemY + ITEM_HEIGHT);
					
					if (mouseX >= scrollXStart &&
							mouseX <= this.width / 2 &&
							mouseY >= itemMouseYStart &&
							mouseY <= itemMouseYEnd) {
						String descKey = setting.description();
						
						if (descKey != null && descKey.startsWith("customspawn.config.")) {
							
							if (descKey.equals("customspawn.config.suitableBiome.desc")) {
								hoveredDescription = String.format(I18n.getString(descKey),
																   LocalizationUtil.localizeSettingName(setting));
							}
							else {
								hoveredDescription = I18n.getString(descKey);
							}
						}
						else {
							hoveredDescription = descKey;
						}
					}
					
					this.fontRenderer.drawString(LocalizationUtil.localizeSettingName(setting),
												 scrollXStart + 2,
												 itemY + 6,
												 0xFFFFFF);
					
					int controlX = scrollXEnd - 30;
					int controlY = itemY + (ITEM_HEIGHT - BUTTON_HEIGHT) / 2;
					
					if (setting.type() == ConfigUtils.Type.BOOLEAN) {
						boolean val = ConfigUtils.getBoolean(setting.id());
						String buttonText = val
											? "§a" + I18n.getString("customspawn.true")
											: "§c" + I18n.getString("customspawn.false");
						
						GuiButton toggleButton = new GuiButton(buttonIDStart + (originalIndex * 20),
															   controlX - BUTTON_WIDTH - 2,
															   controlY,
															   BUTTON_WIDTH + 2,
															   BUTTON_HEIGHT,
															   buttonText);
						toggleButton.drawButton(this.mc, mouseX, mouseY);
						this.buttonList.add(toggleButton);
					}
					else if (setting.type() == ConfigUtils.Type.INT) {
						int val = ConfigUtils.getInt(setting.id());
						String valText = String.valueOf(val);
						
						this.drawCenteredString(this.fontRenderer,
												valText,
												controlX - BUTTON_WIDTH / 2 - 1,
												itemY + 7,
												0xFFFFFF);
						
						GuiButton decButton = new GuiButton(buttonIDStart + (originalIndex * 20),
															controlX - BUTTON_WIDTH - 2,
															controlY,
															20,
															BUTTON_HEIGHT,
															"-");
						decButton.drawButton(this.mc, mouseX, mouseY);
						this.buttonList.add(decButton);
						
						GuiButton incButton = new GuiButton(buttonIDStart + (originalIndex * 20) + 1,
															controlX - BUTTON_WIDTH - 2 + BUTTON_WIDTH - 20 + 2,
															controlY,
															20,
															BUTTON_HEIGHT,
															"+");
						incButton.drawButton(this.mc, mouseX, mouseY);
						this.buttonList.add(incButton);
					}
					else if (setting.type() == ConfigUtils.Type.STRING) {
						String val = ConfigUtils.getString(setting.id());
						String displayVal = LocalizationUtil.localizeBiome(val);
						
						GuiButton previous = new GuiButton(buttonIDStart + (originalIndex * 20),
														   controlX -
																   51 -
																   12 -
																   Math.max(40,
																			fontRenderer.getStringWidth(displayVal) /
																					2 + 12),
														   controlY,
														   20,
														   BUTTON_HEIGHT,
														   "<");
						previous.drawButton(this.mc, mouseX, mouseY);
						this.buttonList.add(previous);
						
						GuiButton next = new GuiButton(buttonIDStart + (originalIndex * 20) + 1,
													   controlX - 50 - 10 +
															   Math.max(40,
																		fontRenderer.getStringWidth(displayVal) / 2 +
																				12),
													   controlY,
													   20,
													   BUTTON_HEIGHT,
													   ">");
						next.drawButton(this.mc, mouseX, mouseY);
						this.buttonList.add(next);
						
						this.drawCenteredString(this.fontRenderer,
												displayVal,
												controlX - BUTTON_WIDTH / 2 - 1,
												itemY + 7,
												0xFFFFFF);
					}
					else if (setting.type() == ConfigUtils.Type.MUL_STRING) {
						String value = ConfigUtils.getString(setting.id());
						String[] parts = value.split(",");
						
						int i1 = 0;
						
						for (String val : parts) {
							String displayVal = LocalizationUtil.localizeBiome(val);
							boolean b = i1 == 0 && parts.length == 20 - 3;
							this.drawCenteredString(this.fontRenderer,
													b
													? "max 16"
													: i1 != 0 ? setting.id()
																		.equalsIgnoreCase(ConfigUtils.wantedBiomesInSpawnKey)
																? "§a" + displayVal
																: setting.id()
																		  .equalsIgnoreCase(ConfigUtils.unwantedBiomesInSpawnKey)
																  ? "§c" + displayVal
																  : displayVal : displayVal,
													controlX - BUTTON_WIDTH / 2 - 1,
													itemY + 7,
													b ? 0xFF0000 : 0xFFFFFF);
							
							if (i1 == 0) {
								GuiButton previous = new GuiButton(buttonIDStart + (originalIndex * 20),
																   controlX -
																		   50 -
																		   12 -
																		   Math.max(40,
																					fontRenderer.getStringWidth(
																							displayVal) / 2 + 12),
																   controlY,
																   20,
																   BUTTON_HEIGHT,
																   "<");
								previous.drawButton(this.mc, mouseX, mouseY);
								this.buttonList.add(previous);
								
								GuiButton next = new GuiButton(buttonIDStart + (originalIndex * 20) + 1,
															   controlX - 50 - 10 +
																	   Math.max(40,
																				fontRenderer.getStringWidth(displayVal) /
																						2 + 12),
															   controlY,
															   20,
															   BUTTON_HEIGHT,
															   ">");
								next.drawButton(this.mc, mouseX, mouseY);
								this.buttonList.add(next);
								
								GuiButton add = new GuiButton(buttonIDStart + (originalIndex * 20) + 2,
															  controlX - 50 - 10 +
																	  (parts.length >= 2
																	   ? Math.max(40,
																				  fontRenderer.getStringWidth(
																						  LocalizationUtil.localizeBiome(
																								  parts[1])) /
																						  2 + 12)
																	   : 40),
															  controlY + ITEM_HEIGHT,
															  20,
															  20,
															  "+");
								
								add.drawButton(this.mc, mouseX, mouseY);
								add.enabled = !val.isEmpty() && parts.length < 20 - 3 && !val.equalsIgnoreCase("none");
								this.buttonList.add(add);
							}
							
							if (i1 != 0) {
								GuiButton remove = new GuiButton(buttonIDStart + (originalIndex * 20) + 2 + i1,
																 controlX -
																		 50 -
																		 12 -
																		 Math.max(40,
																				  fontRenderer.getStringWidth(displayVal) /
																						  2 + 12),
																 controlY + (ITEM_HEIGHT) * i1,
																 20,
																 20,
																 "-");
								remove.drawButton(this.mc, mouseX, mouseY);
								this.buttonList.add(remove);
							}
							
							i1++;
							itemY += ITEM_HEIGHT;
						}
						
						if (i1 == 1) {
							itemY += ITEM_HEIGHT;
						}
						
						itemY -= ITEM_HEIGHT;
					}
					
					itemY += ITEM_HEIGHT;
				}
			}
		}
		
		// 5. Scrollbar
		{
			int barHeight = (int) (scrollHeight * (scrollHeight / (float) contentHeight));
			if (barHeight < 10) barHeight = 10;
			if (contentHeight < scrollHeight) barHeight = scrollHeight;
			
			int maxBarTravel = scrollHeight - barHeight;
			int barYStart = scrollYStart + (int) (scrollOffset * maxBarTravel);
			
			drawRect(scrollXEnd, scrollYStart, scrollXEnd + SCROLLBAR_WIDTH, scrollYEnd, 0x88FFFFFF);
			drawRect(scrollXEnd, barYStart, scrollXEnd + SCROLLBAR_WIDTH, barYStart + barHeight, 0xFFFFFFFF);
		}
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.clearClipping();
		
		GuiButton done = new GuiButton(200, this.width / 2 - 100, this.height - 25, I18n.getString("gui.done"));
		this.buttonList.add(done);
		done.drawButton(this.mc, mouseX, mouseY);
		
		int width = fontRenderer.getStringWidth(I18n.getString("customspawn.save")) + 8;
		
		if (saved && timeSinceSaved + 1000 > System.currentTimeMillis()) {
			this.drawString(this.fontRenderer,
							I18n.getString("customspawn.saved"),
							this.width - width - BUTTON_WIDTH / 2,
							this.height - BUTTON_HEIGHT,
							0xFFFFFF);
		}
		
		GuiButton save = new GuiButton(buttonIDStart - 10,
									   this.width - width - 5,
									   this.height - BUTTON_HEIGHT - 5,
									   width,
									   BUTTON_HEIGHT,
									   I18n.getString("customspawn.save"));
		save.drawButton(this.mc, mouseX, mouseY);
		this.buttonList.add(save);
		
		GuiButton reset = new GuiButton(buttonIDStart - 11,
										this.width - width - 5,
										this.height - 2 * BUTTON_HEIGHT - 9,
										width,
										BUTTON_HEIGHT,
										I18n.getString("customspawn.reset"));
		reset.drawButton(this.mc, mouseX, mouseY);
		this.buttonList.add(reset);
		
		
		if (hoveredDescription != null && !hoveredDescription.isEmpty()) {
			drawTooltip(hoveredDescription, mouseX, mouseY);
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		
		int scrollYEnd = this.height - 40;
		int scrollXEnd = (int) (this.width / 2d + Math.max(150, this.width / 3.5d));
		
		if (mouseX >= scrollXEnd &&
				mouseX <= scrollXEnd + SCROLLBAR_WIDTH &&
				mouseY >= HEADER_HEIGHT &&
				mouseY <= scrollYEnd) {
			
			isScrolling = true;
		}
	}
	
	@Override
	protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
		super.mouseMovedOrUp(mouseX, mouseY, button);
		if (button == 0) {
			isScrolling = false;
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button.id == 200) {
				this.mc.displayGuiScreen(this.parentScreen);
			}
			else if (button.id >= buttonIDStart) {
				handleSettingInteraction(button.id);
			}
			else if (button.id == buttonIDStart - 10) {
				ConfigUpdater.saveConfig(CustomSpawnAddon.getInstance().addonConfig);
				saved = true;
				timeSinceSaved = System.currentTimeMillis();
			}
			else if (button.id == buttonIDStart - 11) {
				for (BaseSetting setting : settingList) {
					ConfigUtils.setValue(setting.id(), setting.defaultValue());
				}
			}
		}
	}
	
	@Override
	public void initGui() {
		this.buttonList.clear();
	}
	
	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		
		int wheel = Mouse.getDWheel();
		int numCategories = categorizedSettings.size();
		int scrollYEnd = this.height - 40;
		float scrollHeight = scrollYEnd - HEADER_HEIGHT;
		int contentHeight = getContentHeight(numCategories);
		float maxScroll = Math.max(0, contentHeight - scrollHeight);
		
		if (wheel != 0) {
			float scrollChange = (float) (wheel / 120) * (ITEM_HEIGHT);
			float offsetChange = scrollChange / maxScroll;
			scrollOffset -= offsetChange;
			scrollOffset = Math.max(0, Math.min(1, scrollOffset));
		}
		
		if (isScrolling) {
			int currentMouseY = Mouse.getY() * this.height / this.mc.displayHeight;
			scrollOffset = (-currentMouseY + this.height - HEADER_HEIGHT) / scrollHeight;
			scrollOffset = Math.max(0, Math.min(1, scrollOffset));
		}
	}
	
	@Override
	public void onGuiClosed() {
		ConfigUtils.setValue(ConfigUtils.unwantedBiomesInSpawnKey,
							 CustomSpawnAddon.unwantedBiomesInSpawn.toString()
									 .replace(" ", "")
									 .replace("[", "")
									 .replace("]", ""));
		ConfigUtils.setValue(ConfigUtils.wantedBiomesInSpawnKey,
							 CustomSpawnAddon.wantedBiomesInSpawn.toString()
									 .replace(" ", "")
									 .replace("[", "")
									 .replace("]", ""));
		saved = false;
	}
	
	private int getContentHeight(int numCategories) {
		String[] wantedBiomes = ConfigUtils.getString(ConfigUtils.wantedBiomesInSpawnKey).split(",");
		String[] unwantedBiomes = ConfigUtils.getString(ConfigUtils.unwantedBiomesInSpawnKey).split(",");
		
		int i = (unwantedBiomes.length == 1 ? ITEM_HEIGHT : (unwantedBiomes.length - 1) * ITEM_HEIGHT) +
				(wantedBiomes.length == 1 ? ITEM_HEIGHT : (wantedBiomes.length - 1) * ITEM_HEIGHT);
		
		return (settingList.size() * ITEM_HEIGHT) + i + (numCategories * SECTION_HEADER_HEIGHT);
	}
	
	private void drawTooltip(String text, int x, int y) {
		int padding = 3;
		int textWidth = this.fontRenderer.getStringWidth(text);
		int tooltipWidth = textWidth + 2 * padding;
		
		int tooltipX = x + 12;
		int tooltipY = y - 10;
		
		if (tooltipX + tooltipWidth + padding > this.width) {
			tooltipX = x - 12 - tooltipWidth;
		}
		
		int bgColor = 0xF0100010;
		int borderColor = 0x505000FF;
		
		drawRect(tooltipX - padding,
				 tooltipY - padding,
				 tooltipX + textWidth + padding,
				 tooltipY + this.fontRenderer.FONT_HEIGHT + padding,
				 bgColor);
		
		drawRect(tooltipX - padding,
				 tooltipY - padding - 1,
				 tooltipX + textWidth + padding,
				 tooltipY - padding,
				 borderColor);
		drawRect(tooltipX - padding,
				 tooltipY + this.fontRenderer.FONT_HEIGHT + padding,
				 tooltipX + textWidth + padding,
				 tooltipY + this.fontRenderer.FONT_HEIGHT + padding + 1,
				 borderColor);
		
		drawRect(tooltipX - padding - 1,
				 tooltipY - padding,
				 tooltipX - padding,
				 tooltipY + this.fontRenderer.FONT_HEIGHT + padding,
				 borderColor);
		drawRect(tooltipX + textWidth + padding,
				 tooltipY - padding,
				 tooltipX + textWidth + padding + 1,
				 tooltipY + this.fontRenderer.FONT_HEIGHT + padding,
				 borderColor);
		
		this.fontRenderer.drawStringWithShadow(text, tooltipX, tooltipY, 0xFFFFFF);
	}
}