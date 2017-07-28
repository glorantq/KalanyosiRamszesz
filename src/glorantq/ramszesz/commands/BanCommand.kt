package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import glorantq.ramszesz.config.ConfigFile
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder

/**
 * Created by glora on 2017. 07. 23..
 */
class BanCommand : ICommand {
    override val commandName: String
        get() = "ban"
    override val description: String
        get() = "Ban a user from the server"
    override val permission: Permission
        get() = Permission.ADMIN
    override val extendedHelp: String
        get() = "Ban a user from the server. Mention a user and optionally specify a reason"
    override val usage: String
        get() = "@Mention [Reason]"

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val embed: EmbedBuilder = BotUtils.embed("Ban", event.author)
        if(!BotUtils.hasPermissions(4, event.author, event.guild)) {
            embed.withDescription("You don't have permissions to ban users!")
            BotUtils.sendMessage(embed.build(), event.channel)
            return
        }

        val mentions: List<IUser> = event.message.mentions
        if (mentions.isEmpty()) {
            BotUtils.sendUsageEmbed("You need to mention a user!", "Ban", event.author, event, this)
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

            val config: ConfigFile = BotUtils.getGuildConfig(event)
            if(config.logModerations) {
                event.guild.getChannelByID(config.modLogChannel).sendMessage(embed.build())
            }
        } catch (e: Exception) {
            embed.withDescription("Failed to ban user!")
            embed.appendField(e::class.simpleName, e.message, false)
        }

        BotUtils.sendMessage(embed.build(), event.channel)
    }
}