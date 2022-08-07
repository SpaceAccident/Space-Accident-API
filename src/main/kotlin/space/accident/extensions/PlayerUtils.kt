package space.accident.extensions

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ChatComponentText

object PlayerUtils {

    @JvmStatic
    fun EntityPlayer?.sendChat(msg: String) {
        if (this == null) return
        this.addChatComponentMessage(ChatComponentText(msg))
    }
}