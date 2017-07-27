package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import glorantq.ramszesz.inviteUrl
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder

/**
 * Created by glora on 2017. 07. 23..
 */
class JoinCommand : ICommand {
    override val commandName: String
        get() = "invite"
    override val description: String
        get() = "Get a link to invite this bot to your server"
    override val permission: Permission
        get() = Permission.NONE
    override val availabeInDM: Boolean
        get() = true

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val builder: EmbedBuilder = BotUtils.embed("Invite", event.author)
        builder.withUrl(inviteUrl)
        builder.appendField("Use this link to add me to your server!", inviteUrl, false)

        event.channel.sendMessage(builder.build())
    }
}