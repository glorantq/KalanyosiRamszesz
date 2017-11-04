package glorantq.ramszesz.scripting.api

import org.luaj.vm2.LuaTable
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

class LuaMessageEvent(event: MessageReceivedEvent) : LuaTable() {
    init {
        set("channel", LuaChannel(event.channel))
    }
}