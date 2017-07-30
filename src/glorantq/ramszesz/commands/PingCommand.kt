package glorantq.ramszesz.commands

import glorantq.ramszesz.utils.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

/**
 * Created by glorantq on 2017. 07. 22..
 */
class PingCommand : ICommand {
    override val commandName: String
        get() = "ping"
    override val description: String
        get() = "Pong!"
    override val permission: Permission
        get() = Permission.USER

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        BotUtils.sendMessage(BotUtils.createSimpleEmbed("Pong!", args.toString(), event.author), event.channel)
    }
}