package space.accident.api.graphs.consumers;

import net.minecraft.tileentity.TileEntity;
import space.accident.api.graphs.paths.PowerNodePath;
import space.accident.api.metatileentity.base.BaseMetaPipeEntity;

import java.util.ArrayList;

//this is here to apply voltage to dead ends
public class EmptyPowerConsumer extends ConsumerNode{
    public EmptyPowerConsumer(int aNodeValue, TileEntity aTileEntity, int side, ArrayList<ConsumerNode> aConsumers) {
        super(aNodeValue, aTileEntity, side, aConsumers);
    }

    @Override
    public boolean needsEnergy() {
        return false;
    }

    @Override
    public int injectEnergy(long aVoltage, long aMaxAmps) {
        BaseMetaPipeEntity tPipe = (BaseMetaPipeEntity) mTileEntity;
        PowerNodePath tPath =(PowerNodePath) tPipe.getNodePath();
        tPath.applyVoltage(aVoltage,true);
        return 0;
    }
}
