package space.accident.api.graphs;

import net.minecraft.tileentity.TileEntity;
import space.accident.api.graphs.consumers.*;
import space.accident.api.graphs.paths.NodePath;
import space.accident.api.graphs.paths.PowerNodePath;
import space.accident.api.interfaces.tileentity.energy.IEnergyTileConnected;
import space.accident.api.metatileentity.base.BaseMetaPipeEntity;
import space.accident.api.metatileentity.base.BaseMetaTileEntity;
import space.accident.api.metatileentity.base.MetaPipeEntity;
import space.accident.api.metatileentity.implementations.logistic.Cable_Electricity;

import java.util.ArrayList;
import java.util.HashSet;

// node map generator for power distribution
public class GenerateNodeMapPower extends GenerateNodeMap {
    public GenerateNodeMapPower(BaseMetaPipeEntity aTileEntity) {
        generateNode(aTileEntity, null, 1, null, -1, new ArrayList<>(), new HashSet<>());
    }
    
    @Override
    protected boolean isPipe(TileEntity aTileEntity) {
        return super.isPipe(aTileEntity) && ((BaseMetaPipeEntity) aTileEntity).getMetaTile() instanceof Cable_Electricity;
    }
    
    @Override
    protected boolean addConsumer(TileEntity aTileEntity, int side, int aNodeValue, ArrayList<ConsumerNode> aConsumers) {
        if (aTileEntity instanceof BaseMetaTileEntity) {
            BaseMetaTileEntity tBaseTileEntity = (BaseMetaTileEntity) aTileEntity;
            if (tBaseTileEntity.inputEnergyFrom(side, false)) {
                ConsumerNode tConsumerNode = new NodeBaseTile(aNodeValue, tBaseTileEntity, side, aConsumers);
                aConsumers.add(tConsumerNode);
                return true;
            }
            
        } else if (aTileEntity instanceof IEnergyTileConnected) {
            IEnergyTileConnected tTileEntity = (IEnergyTileConnected) aTileEntity;
            if (tTileEntity.inputEnergyFrom(side, false)) {
                ConsumerNode tConsumerNode = new NodeEnergyConnected(aNodeValue, tTileEntity, side, aConsumers);
                aConsumers.add(tConsumerNode);
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected NodePath getNewPath(MetaPipeEntity[] aPipes) {
        return new PowerNodePath(aPipes);
    }
    
    //used to apply voltage on dead ends
    @Override
    protected Node getEmptyNode(int aNodeValue, int side, TileEntity aTileEntity, ArrayList<ConsumerNode> aConsumers) {
        ConsumerNode tNode = new EmptyPowerConsumer(aNodeValue, aTileEntity, side, aConsumers);
        aConsumers.add(tNode);
        return tNode;
    }
    
    @Override
    protected Node getPipeNode(int aNodeValue, int side, TileEntity aTileEntity, ArrayList<ConsumerNode> aConsumers) {
        return new PowerNode(aNodeValue, aTileEntity, aConsumers);
    }
}
