package space.accident.api.util

import com.google.common.io.ByteArrayDataInput
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import java.io.IOException

interface ISerializableObject {


    fun copy(): ISerializableObject

    /**
     * If you are overriding this, you must **NOT** return [NBTTagInt] here! That return
     * type is how we tell that we are loading legacy data, and only [LegacyCoverData] is
     * allowed to return it. You probably want to return [NBTTagCompound] anyway.
     */
    fun saveDataToNBT(): NBTBase

    /**
     * Write data to given ByteBuf
     * The data saved this way is intended to be stored for short amount of time over network.
     * DO NOT store it to disks.
     */
    // the NBT is an unfortunate piece of tech. everything uses it but its API is not as efficient as could be
    fun writeToByteBuf(aBuf: ByteBuf)

    fun loadDataFromNBT(aNBT: NBTBase)

    /**
     * Read data from given parameter and return this.
     * The data read this way is intended to be stored for short amount of time over network.
     * @param aPlayer the player who is sending this packet to server. null if it's client reading data.
     */
    // the NBT is an unfortunate piece of tech. everything uses it but its API is not as efficient as could be
    fun readFromPacket(aBuf: ByteArrayDataInput, aPlayer: EntityPlayerMP): ISerializableObject

    companion object {
        /**
         * Reverse engineered and adapted [cpw.mods.fml.common.network.ByteBufUtils.readTag]
         * Given buffer must contain a serialized NBTTagCompound in minecraft encoding
         */
        fun readCompoundTagFromGreggyByteBuf(aBuf: ByteArrayDataInput): NBTTagCompound? {
            val size = aBuf.readShort()
            return if (size < 0) null else {
                val buf = ByteArray(size.toInt()) // this is shit, how many copies have we been doing?
                aBuf.readFully(buf)
                try {
                    CompressedStreamTools.func_152457_a(buf, NBTSizeTracker(2097152L))
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
        }

        /**
         * Reverse engineered and adapted [cpw.mods.fml.common.network.ByteBufUtils.readItemStack]
         * Given buffer must contain a serialized ItemStack in minecraft encoding
         */
        fun readItemStackFromGreggyByteBuf(aBuf: ByteArrayDataInput): ItemStack? {
            var stack: ItemStack? = null
            val id = aBuf.readShort()
            if (id >= 0) {
                val size = aBuf.readByte()
                val meta = aBuf.readShort()
                stack = ItemStack(Item.getItemById(id.toInt()), size.toInt(), meta.toInt())
                stack.stackTagCompound = readCompoundTagFromGreggyByteBuf(aBuf)
            }
            return stack
        }
    }

    class LegacyCoverData @JvmOverloads constructor(var mData: Int = 0) : ISerializableObject {

        override fun copy(): ISerializableObject {
            return LegacyCoverData(mData)
        }

        override fun saveDataToNBT(): NBTBase {
            return NBTTagInt(mData)
        }

        override fun writeToByteBuf(aBuf: ByteBuf) {
            aBuf.writeInt(mData)
        }

        override fun loadDataFromNBT(aNBT: NBTBase) {
            mData = if (aNBT is NBTTagInt) aNBT.func_150287_d() else 0
        }

        override fun readFromPacket(aBuf: ByteArrayDataInput, aPlayer: EntityPlayerMP): ISerializableObject {
            mData = aBuf.readInt()
            return this
        }

        fun get(): Int {
            return mData
        }

        fun set(mData: Int) {
            this.mData = mData
        }

        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false
            val that = o as LegacyCoverData
            return mData == that.mData
        }

        override fun hashCode(): Int {
            return mData
        }

        override fun toString(): String {
            return mData.toString()
        }
    }
}