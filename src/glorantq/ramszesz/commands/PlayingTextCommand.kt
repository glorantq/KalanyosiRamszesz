package glorantq.ramszesz.commands

import glorantq.ramszesz.utils.BotUtils
import glorantq.ramszesz.Ramszesz
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

/**
 * Created by glora on 2017. 07. 23..
 */
class PlayingTextCommand : ICommand {
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
    override val availableInDM: Boolean
        get() = true

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val builder: StringBuilder = StringBuilder()

        for(part: String in args) {
            builder.append(part)
            builder.append(" ")
        }

        var status: String = builder.toString().trim()
        status = when(status) {
            "guild_count" -> buildString {
                append("in ")
                append(event.client.guilds.size)
                append(" guilds")
            }

            "user_count" -> buildString {
                var users: Int = 0
                event.client.guilds.forEach { users += it.users.size }
                append("with ")
                append(users)
                append(" users")
            }

            else -> if(status.isEmpty()) { "default" } else { status }
        }

        if(status.equals("default", true)) {
            Ramszesz.instance.updatePlayingText = true
        } else {
            Ramszesz.instance.updatePlayingText = false
            event.client.changePlayingText(status)
        }

        BotUtils.sendMessage(BotUtils.createSimpleEmbed("Change Playing Text", "Successfully changed playing text to: `$status`", event.author), event.channel)
    }
}