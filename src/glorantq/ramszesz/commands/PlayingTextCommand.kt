package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

/**
 * Created by glora on 2017. 07. 23..
 */
class PlayingTextCommand : Command {
    override val commandName: String
        get() = "playingtext"
    override val description: String
        get() = "Change the playing text"
    override val permission: Permission
        get() = Permission.BOT_OWNER
    override val aliases: List<String>
        get() = listOf("playing", "pt")
    override val undocumented: Boolean
        get() = true

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val builder: StringBuilder = StringBuilder()

        for(part: String in args) {
            builder.append(part)
            builder.append(" ")
        }

        var status: String = builder.toString().trim()
        if(status.isEmpty()) status = "with Viktor's stuff"
        event.client.changePlayingText(status)

        event.channel.sendMessage(BotUtils.createSimpleEmbed("Change Playing Text", "Successfully changed playing text to: `$status`", event.author))
    }
}