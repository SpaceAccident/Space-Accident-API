package space.accident.api.graphs.consumers;

import net.minecraft.tileentity.TileEntity;
import space.accident.api.interfaces.tileentity.energy.IEnergyTileConnected;

import java.util.ArrayList;

public class NodeEnergyConnected extends ConsumerNode {
    public NodeEnergyConnected(int aNodeValue, IEnergyTileConnected aTileEntity, int side, ArrayList<ConsumerNode> aConsumers) {
        super(aNodeValue,(TileEntity) aTileEntity, side, aConsumers);
    }

    @Override
    public boolean needsEnergy() {
        return super.needsEnergy();
    }

    @Override
    public int injectEnergy(long aVoltage, long aMaxAmps) {
        return (int) ((IEnergyTileConnected)mTileEntity).injectEnergyUnits(mSide,aVoltage,aMaxAmps);
    }
}
