package space.accident.api.interfaces.tileentity;

import space.accident.api.interfaces.ITexture;

public interface IPipeRenderedTileEntity extends ICoverable, ITexturedTileEntity {
	float getThickNess();
	
	int getConnections();
	
	ITexture[] getTextureUncovered(int side);
	
	default ITexture[] getTextureCovered(int side) {
		return getTextureUncovered(side);
	}
}
