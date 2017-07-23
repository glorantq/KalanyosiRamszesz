package glorantq.ramszesz.commands

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

/**
 * Created by glorantq on 2017. 07. 23..
 */
class LeaveCommand : Command {
    override val commandName: String
        get() = "leave"
    override val description: String
        get() = "Leaves the server"
    override val permission: Permission
        get() = Permission.OWNER

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        event.channel.sendMessage("Goodbye!")
    }
}