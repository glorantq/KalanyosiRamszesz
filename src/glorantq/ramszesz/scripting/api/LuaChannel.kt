package glorantq.ramszesz.scripting.api

import glorantq.ramszesz.utils.BotUtils
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import sx.blah.discord.handle.obj.IChannel

class LuaChannel(val channel: IChannel) : LuaTable() {
    init {
        set("name", LuaValue.valueOf(channel.name ?: ""))
        set("topic", LuaValue.valueOf(channel.topic ?: ""))
        set("mention", LuaValue.valueOf(channel.mention() ?: ""))
        set("isNSFW", LuaValue.valueOf(channel.isNSFW))
        set("longID", LuaValue.valueOf(channel.longID.toString()))
        set("isDM", LuaValue.valueOf(channel.isPrivate))

        set("sendMessage", SendMessage())
    }

    private inner class SendMessage : OneArgFunction() {
        override fun call(p0: LuaValue): LuaValue {
            when {
                p0.isstring() -> {
                    val message: String = p0.checkjstring()
                    BotUtils.sendMessage(message, channel)

                    return LuaValue.TRUE
                }
                p0 is LuaEmbed -> BotUtils.sendMessage(p0.embed, channel)
                else -> BotUtils.sendMessage(p0.tojstring(), channel)
            }

            return LuaValue.NIL
        }
    }
}