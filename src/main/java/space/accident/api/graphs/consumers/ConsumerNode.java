package space.accident.api.graphs.consumers;

import net.minecraft.tileentity.TileEntity;
import space.accident.api.graphs.Node;

import java.util.ArrayList;

// node attached to a tile entity that can consume stuff from the network
public class ConsumerNode extends Node {
    public int mSide;
    public ConsumerNode(int aNodeValue, TileEntity aTileEntity, int side, ArrayList<ConsumerNode> aConsumers) {
        super(aNodeValue,aTileEntity,aConsumers);
        this.mSide = side;
    }

    public boolean needsEnergy() {
        return !mTileEntity.isInvalid();
    }

    public int injectEnergy(long aVoltage, long aMaxAmps) {
        return 0;
    }
}
