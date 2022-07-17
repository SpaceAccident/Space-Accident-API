package space.accident.api.interfaces.te

import net.minecraft.block.Block

interface ITexturedTileEntity {
    /**
     * @return the Textures rendered by the GT Rendering
     */
    fun getTexture(aBlock: Block?, aSide: Byte): Array<ITexture>
}
