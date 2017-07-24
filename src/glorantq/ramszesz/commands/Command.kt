package glorantq.ramszesz.commands

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import java.util.*

/**
 * Created by glorantq on 2017. 07. 22..
 */
interface Command {
    val commandName: String
    val description: String
    val extendedHelp: String
        get() = "No more help available"
    val aliases: List<String>
        get() = ArrayList()
    val undocumented: Boolean
        get() = false
    val permission: Permission
    val usage: String
        get() = ""

    fun execute(event: MessageReceivedEvent, args: List<String>)
}