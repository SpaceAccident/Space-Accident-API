package space.accident.structurelib.structure;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Use StructureUtility to instantiate
 */
public interface IStructureElement<T> {
    boolean check(T t, World world, int x, int y, int z);

    boolean spawnHint(T t, World world, int x, int y, int z, ItemStack trigger);

    boolean placeBlock(T t, World world, int x, int y, int z, ItemStack trigger);

    default int getStepA() {
        return 1;
    }

    default int getStepB() {
        return 0;
    }

    default int getStepC() {
        return 0;
    }

    default boolean resetA() {
        return false;
    }

    default boolean resetB() {
        return false;
    }

    default boolean resetC() {
        return false;
    }

    default boolean isNavigating() {
        return false;
    }
}
