package space.accident.extensions

import net.minecraft.init.Blocks
import net.minecraft.world.World

object WorldUtil {

    @JvmStatic
    fun World?.isOpaqueBlock(x: Int, y: Int, z: Int): Boolean {
        return this?.getBlock(x, y, z)?.isOpaqueCube ?: false
    }

    @JvmStatic
    fun World?.isBlockAir(x: Int, y: Int, z: Int): Boolean {
        return this?.getBlock(x, y, z)?.isAir(this, x, y, z) ?: false
    }

    @JvmStatic
    fun World?.setCoordsOnFire(x: Int, y: Int, z: Int, needReplaceCenter: Boolean) {
        if (this == null) return
        val world = this
        if (needReplaceCenter) {
            if (world.getBlock(x, y, z).getCollisionBoundingBoxFromPool(world, x, y, z) == null) world.setBlock(x, y, z, Blocks.fire)
        }
        if (world.getBlock(x + 1, y, z).getCollisionBoundingBoxFromPool(world, x + 1, y, z) == null) world.setBlock(x + 1, y, z, Blocks.fire)
        if (world.getBlock(x - 1, y, z).getCollisionBoundingBoxFromPool(world, x - 1, y, z) == null) world.setBlock(x - 1, y, z, Blocks.fire)
        if (world.getBlock(x, y + 1, z).getCollisionBoundingBoxFromPool(world, x, y + 1, z) == null) world.setBlock(x, y + 1, z, Blocks.fire)
        if (world.getBlock(x, y - 1, z).getCollisionBoundingBoxFromPool(world, x, y - 1, z) == null) world.setBlock(x, y - 1, z, Blocks.fire)
        if (world.getBlock(x, y, z + 1).getCollisionBoundingBoxFromPool(world, x, y, z + 1) == null) world.setBlock(x, y, z + 1, Blocks.fire)
        if (world.getBlock(x, y, z - 1).getCollisionBoundingBoxFromPool(world, x, y, z - 1) == null) world.setBlock(x, y, z - 1, Blocks.fire)
    }
}