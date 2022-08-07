package space.accident.main.render;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import space.accident.api.interfaces.ITexture;
import space.accident.api.util.Utility;

public abstract class TextureBase implements ITexture {
	protected boolean isDrawing = false;
	
	@Override
	public void startDrawingQuads(RenderBlocks aRenderer, float aNormalX, float aNormalY, float aNormalZ) {
		if (aRenderer.useInventoryTint && (!isOldTexture() || !Utility.isDrawing(Tessellator.instance))) {
			// Draw if we're not an old texture OR we are an old texture AND we're not already drawing
			isDrawing = true;
			Tessellator.instance.startDrawingQuads();
			Tessellator.instance.setNormal(aNormalX, aNormalY, aNormalZ);
		}
	}
	
	@Override
	public void draw(RenderBlocks aRenderer) {
		if (aRenderer.useInventoryTint && (!isOldTexture() || isDrawing)) {
			// Draw if we're not an old texture OR we initiated the drawing
			isDrawing = false;
			Tessellator.instance.draw();
		}
	}
}
