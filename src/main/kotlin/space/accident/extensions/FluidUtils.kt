package space.accident.extensions

import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import space.accident.extensions.StringUtils.capitalizeString

object FluidUtils {

    @JvmStatic
    public fun Fluid?.getFluidName(aLocalized: Boolean): String {
        if (this == null) return ""
        val rName = if (aLocalized) this.getLocalizedName(FluidStack(this, 0)) else this.unlocalizedName
        return if (rName.contains("fluid.") || rName.contains("tile.")) {
            rName.replace("fluid.".toRegex(), "")
                .replace("tile.".toRegex(), "")
                .capitalizeString()
        } else rName
    }

    @JvmStatic
    public fun FluidStack?.getFluidName(aLocalized: Boolean): String {
        return this?.getFluid()?.getFluidName(aLocalized) ?: ""
    }

    @JvmStatic
    public fun FluidStack?.isWater(): Boolean {
        return this?.isFluidEqual(FluidRegistry.getFluidStack("water", 1)) ?: false
    }
}