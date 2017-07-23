package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.PermissionUtils

class KickCommand: Command {
    override val commandName: String
        get() = "kick"
    override val description: String
        get() = "Kick a user"
    override val permission: Permission
        get() = Permission.ADMIN
    override val extendedHelp: String
        get() = "Kick a user. Mention a user and optionally specify a reason"
    override val aliases: List<String>
        get() = super.aliases

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val embed: EmbedBuilder = BotUtils.embed("Kick", event.author)
        val canKick: Boolean = event.author.getPermissionsForGuild(event.guild).any { it.hasPermission(2) }
        if(!canKick) {
            embed.withDescription("You don't have permissions to kick users!")
            event.channel.sendMessage(embed.build())
            return
        }

        val mentions: List<IUser> = event.message.mentions
        if (mentions.isEmpty()) {
            embed.withDescription("You need to mention a user!")
            embed.appendField("Usage", "`r!kick @Mention [Reason]`", false)
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
            event.guild.kickUser(mentions[0], reasonString)
            if(reasonString.isEmpty()) {
                embed.withDescription("The user `@${mentions[0].name}` has been kicked from the server by ${event.author.mention()}")
            } else {
                embed.withDescription("The user `@${mentions[0].name}` has been kicked from the server by ${event.author.mention()} for: `$reasonString`")
            }
        } catch (e: Exception) {
            embed.withDescription("Failed to kick user!")
            embed.appendField(e::class.simpleName, e.message, false)
        }

        event.channel.sendMessage(embed.build())
    }
}