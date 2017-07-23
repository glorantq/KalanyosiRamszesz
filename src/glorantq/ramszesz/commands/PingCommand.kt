package glorantq.ramszesz.commands

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

/**
 * Created by glorantq on 2017. 07. 22..
 */
class PingCommand : Command {
    override val commandName: String
        get() = "ping"
    override val description: String
        get() = "Pong!"
    override val permission: Permission
        get() = Permission.USER

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        event.channel.sendMessage("Pong! ($args)")
    }
}