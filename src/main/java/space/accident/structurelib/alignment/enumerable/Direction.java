package space.accident.structurelib.alignment.enumerable;

import net.minecraftforge.common.util.ForgeDirection;
import space.accident.structurelib.util.Vec3Impl;

public enum Direction {
    DOWN(ForgeDirection.DOWN),
    UP(ForgeDirection.UP),
    NORTH(ForgeDirection.NORTH),
    SOUTH(ForgeDirection.SOUTH),
    WEST(ForgeDirection.WEST),
    EAST(ForgeDirection.EAST);

    private final ForgeDirection forgeDirection;
    private final Vec3Impl axisVector;
    public static final Direction[] VALUES = values();

    Direction(ForgeDirection forgeDirection) {
        this.forgeDirection = forgeDirection;
        axisVector = new Vec3Impl(forgeDirection.offsetX, forgeDirection.offsetY, forgeDirection.offsetZ);
    }

    public ForgeDirection getForgeDirection() {
        return forgeDirection;
    }

    public Vec3Impl getAxisVector() {
        return axisVector;
    }

    public static Vec3Impl getAxisVector(ForgeDirection forgeDirection) {
        return VALUES[forgeDirection.ordinal()].axisVector;
    }
}
