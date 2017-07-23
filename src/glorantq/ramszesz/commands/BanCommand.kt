package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder

/**
 * Created by glora on 2017. 07. 23..
 */
class BanCommand : Command {
    override val commandName: String
        get() = "ban"
    override val description: String
        get() = "Ban a user from the server"
    override val permission: Permission
        get() = Permission.ADMIN
    override val extendedHelp: String
        get() = "Ban a user from the server. Mention a user and optionally specify a reason"

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val embed: EmbedBuilder = BotUtils.embed("Ban", event.author)
        val canKick: Boolean = event.author.getPermissionsForGuild(event.guild).any { it.hasPermission(4) }
        if(!canKick) {
            embed.withDescription("You don't have permissions to ban users!")
            event.channel.sendMessage(embed.build())
            return
        }

        val mentions: List<IUser> = event.message.mentions
        if (mentions.isEmpty()) {
            embed.withDescription("You need to mention a user!")
            embed.appendField("Usage", "`r!ban @Mention [Reason]`", false)
            event.channel.sendMessage(embed.build())
            return
        }
        val nameLength: Int = mentions[0].name.split(" ").size
        val reason: StringBuilder = StringBuilder()

        if (args.size > nameLength) {
            for(part: String in args.subList(nameLength, args.size)) {
                reason.append(part)
                reason.append(" ")
            }
        }

        val reasonString: String = reason.toString().trim()
        try {
            event.guild.banUser(mentions[0], reasonString)
            if(reasonString.isEmpty()) {
                embed.withDescription("The user `@${mentions[0].name}` has been banned from the server by ${event.author.mention()}")
            } else {
                embed.withDescription("The user `@${mentions[0].name}` has been banned from the server by ${event.author.mention()} for: `$reasonString`")
            }
        } catch (e: Exception) {
            embed.withDescription("Failed to ban user!")
            embed.appendField(e::class.simpleName, e.message, false)
        }

        event.channel.sendMessage(embed.build())
    }
}