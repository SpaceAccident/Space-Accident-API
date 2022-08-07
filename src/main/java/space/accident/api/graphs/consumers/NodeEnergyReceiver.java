package space.accident.api.graphs.consumers;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import space.accident.api.API;
import space.accident.api.util.Utility;
import space.accident.api.util.WorldSpawnedEventBuilder;

import java.util.ArrayList;

import static space.accident.api.enums.Values.V;
import static space.accident.api.util.Utility.getStrength;

//consumer for RF machines
public class NodeEnergyReceiver extends ConsumerNode {
    int mRestRF = 0;
    
    public NodeEnergyReceiver(int aNodeValue, IEnergyReceiver aTileEntity, int side, ArrayList<ConsumerNode> aConsumers) {
        super(aNodeValue, (TileEntity) aTileEntity, side, aConsumers);
    }
    
    @Override
    public int injectEnergy(long aVoltage, long aMaxAmps) {
        ForgeDirection tDirection = ForgeDirection.getOrientation(mSide);
        int rfOut = Utility.safeInt(aVoltage * API.mEUtoRF / 100);
        int ampsUsed = 0;
        if (mRestRF < rfOut) {
            mRestRF += rfOut;
            ampsUsed = 1;
        }
        if (((IEnergyReceiver) mTileEntity).receiveEnergy(tDirection, mRestRF, true) > 0) {
            int consumed = ((IEnergyReceiver) mTileEntity).receiveEnergy(tDirection, mRestRF, false);
            mRestRF -= consumed;
            return ampsUsed;
        }
        if (API.mRFExplosions && API.sMachineExplosions && ((IEnergyReceiver) mTileEntity).getMaxEnergyStored(tDirection) < rfOut * 600L) {
            explode(rfOut);
        }
        return 0;
    }
    
    //copied from IEnergyTileConnected
    private void explode(int aRfOut) {
        if (aRfOut > 32L * API.mEUtoRF / 100L) {
            int aExplosionPower = aRfOut;
            float tStrength = getStrength(aExplosionPower);
            int tX = mTileEntity.xCoord, tY = mTileEntity.yCoord, tZ = mTileEntity.zCoord;
            World tWorld = mTileEntity.getWorldObj();
            Utility.sendSoundToPlayers(tWorld, API.sSoundList.get(209), 1.0F, -1, tX, tY, tZ);
            tWorld.setBlock(tX, tY, tZ, Blocks.air);
//            if (API.sMachineExplosions) {
//                if (API.mPollution) {
//                    GT_Pollution.addPollution(tWorld.getChunkFromBlockCoords(tX, tZ), API.mPollutionOnExplosion);
//                }
//            }
            
            new WorldSpawnedEventBuilder.ExplosionEffectEventBuilder().setStrength(tStrength).setSmoking(true).setPosition(tX + 0.5, tY + 0.5, tZ + 0.5).setWorld(tWorld).run();
        }
    }
}
