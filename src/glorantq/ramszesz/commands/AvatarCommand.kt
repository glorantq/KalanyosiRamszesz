package glorantq.ramszesz.commands

import glorantq.ramszesz.utils.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder

/**
 * Created by glora on 2017. 07. 23..
 */
class AvatarCommand : ICommand {
    override val commandName: String
        get() = "avatar"
    override val description: String
        get() = "Get the avatar of a user"
    override val permission: Permission
        get() = Permission.USER
    override val extendedHelp: String
        get() = "Get the avatar of a user. Tag a person to get their avatar"
    override val aliases: List<String>
        get() = listOf("profilepic", "ppic")
    override val usage: String
        get() = "[@Mention]"

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val mentions: List<IUser> = event.message.mentions
        val username: String
        val avatarUrl: String
        if(mentions.isEmpty()) {
            avatarUrl = event.author.avatarURL
            username = event.author.name
        } else {
            avatarUrl = mentions[0].avatarURL
            username = mentions[0].name
        }

        val builder: EmbedBuilder = BotUtils.embed("Avatar", event.author)
        builder.withDescription("Here's $username's avatar:")
        builder.withImage("$avatarUrl?size=2048")

        BotUtils.sendMessage(builder.build(), event.channel)
    }
}