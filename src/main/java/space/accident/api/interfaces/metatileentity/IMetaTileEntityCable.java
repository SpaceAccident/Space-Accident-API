package space.accident.api.interfaces.metatileentity;

import net.minecraft.tileentity.TileEntity;

import java.util.HashSet;

public interface IMetaTileEntityCable extends IMetaTile {
    long transferElectricity(int side, long aVoltage, long aAmperage, HashSet<TileEntity> aAlreadyPassedSet);
}
