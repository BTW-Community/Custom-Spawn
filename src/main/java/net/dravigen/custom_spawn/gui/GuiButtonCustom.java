package net.dravigen.custom_spawn.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.GuiButton;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ResourceLocation;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class GuiButtonCustom extends GuiButton {
	protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
	private static final int[] uvTexture = new int[2];
	public ResourceLocation resourceLocation;
	
	public GuiButtonCustom(int id, int posX, int posY, int width, int height, int u, int v, String displayText,
			ResourceLocation texture) {
		super(id, posX, posY, width, height, displayText);
		resourceLocation = texture;
		uvTexture[0] = u;
		uvTexture[1] = v;
	}
	
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		minecraft.getTextureManager().bindTexture(buttonTextures);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.field_82253_i = mouseX >= this.xPosition &&
				mouseY >= this.yPosition &&
				mouseX < this.xPosition + this.width &&
				mouseY < this.yPosition + this.height;
		int var5 = this.getHoverState(this.field_82253_i);
		this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + var5 * 20, this.width / 2, this.height);
		this.drawTexturedModalRect(this.xPosition + this.width / 2,
								   this.yPosition,
								   200 - this.width / 2,
								   46 + var5 * 20,
								   this.width / 2,
								   this.height);
		
		minecraft.getTextureManager().bindTexture(resourceLocation);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(this.xPosition, this.yPosition, uvTexture[0], uvTexture[1], this.width, this.height);
	}
}