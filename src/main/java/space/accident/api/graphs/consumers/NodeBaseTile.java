package space.accident.api.graphs.consumers;

import space.accident.api.interfaces.tileentity.energy.IEnergyTileConnected;
import space.accident.api.metatileentity.base.BaseMetaTileEntity;

import java.util.ArrayList;

// consumer for this mod machines
public class NodeBaseTile extends ConsumerNode {
    public NodeBaseTile(int aNodeValue, BaseMetaTileEntity aTileEntity, int side, ArrayList<ConsumerNode> aConsumers) {
        super(aNodeValue, aTileEntity, side, aConsumers);
    }
    
    @Override
    public int injectEnergy(long aVoltage, long aMaxAmps) {
        return (int) ((IEnergyTileConnected) mTileEntity).injectEnergyUnits(mSide, aVoltage, aMaxAmps);
    }
    
    @Override
    public boolean needsEnergy() {
        BaseMetaTileEntity tTileEntity = (BaseMetaTileEntity) mTileEntity;
        return super.needsEnergy() && tTileEntity.getStoredEU() < tTileEntity.getEUCapacity();
    }
}
