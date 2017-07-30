package glorantq.ramszesz.commands

import glorantq.ramszesz.utils.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

/**
 * Created by glorantq on 2017. 07. 23..
 */
class LeaveCommand : ICommand {
    override val commandName: String
        get() = "leave"
    override val description: String
        get() = "Leaves the server"
    override val permission: Permission
        get() = Permission.OWNER

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        BotUtils.sendMessage(BotUtils.createSimpleEmbed("Leave", "Goodbye!", event.author), event.channel)
        event.client.logout()
        System.exit(2)
    }
}