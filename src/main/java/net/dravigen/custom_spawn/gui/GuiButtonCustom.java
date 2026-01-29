package net.dravigen.custom_spawn.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ResourceLocation;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class GuiButtonCustom extends GuiButton {
	public ResourceLocation resourceLocation;
	protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
	public int baseTextureWidth;
	public int baseTextureHeight;
	public GuiButtonCustom(int id, int posX, int posY, int width, int height, int baseWidth, int baseHeight,
			String displayText, ResourceLocation texture) {
		super(id, posX, posY, width, height, displayText);
		resourceLocation = texture;
		baseTextureWidth = baseWidth;
		baseTextureHeight = baseHeight;
	}
	
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		FontRenderer var4 = minecraft.fontRenderer;
		minecraft.getTextureManager().bindTexture(buttonTextures);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.field_82253_i = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
		int var5 = this.getHoverState(this.field_82253_i);
		this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + var5 * 20, this.width / 2, this.height);
		this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + var5 * 20, this.width / 2, this.height);
		
		minecraft.getTextureManager().bindTexture(resourceLocation);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(this.xPosition,
								   this.yPosition,
								   0, 0,
								   this.width / 2,
								   this.height / 2);
		this.drawTexturedModalRect(this.xPosition + this.width / 2,
								   this.yPosition,
								   this.baseTextureWidth - this.width / 2, 0,
								   this.width / 2,
								   this.height / 2);
		this.drawTexturedModalRect(this.xPosition,
								   this.yPosition + this.height / 2,
								   0, this.baseTextureHeight - this.height / 2,
								   this.width / 2,
								   this.height / 2);
		this.drawTexturedModalRect(this.xPosition + this.width / 2,
								   this.yPosition + this.height / 2,
								   this.baseTextureWidth - this.width / 2, this.baseTextureHeight - this.height / 2,
								   this.width / 2,
								   this.height / 2);
		
		int color = 0xffffff;
		if (this.displayString.equalsIgnoreCase("enabled") || this.displayString.equalsIgnoreCase("true")) {
			color = 0x29ff00;
		}
		else if (this.displayString.equalsIgnoreCase("disabled") || this.displayString.equalsIgnoreCase("false")) {
			color = 0xff1100;
		}
		
		this.drawCenteredString(var4,
								this.displayString,
								this.xPosition + this.width / 2,
								this.yPosition + (this.height - 8) / 2,
								color);
		
	}
}