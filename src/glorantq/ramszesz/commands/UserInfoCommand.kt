package glorantq.ramszesz.commands

import glorantq.ramszesz.utils.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IRole
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.handle.obj.StatusType
import sx.blah.discord.util.EmbedBuilder
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Created by glorantq on 2017. 07. 31..
 */
class UserInfoCommand : ICommand {
    override val commandName: String
        get() = "userinfo"
    override val description: String
        get() = "Shows information about a user"
    override val permission: Permission
        get() = Permission.USER
    override val usage: String
        get() = "@Mention"
    override val aliases: List<String>
        get() = listOf("whois", "uinfo", "uinf")

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        if (event.message.mentions.size < 1) {
            BotUtils.sendUsageEmbed("You need to mention a user!", "User Info", event.author, event, this)
            return
        }

        val user: IUser = event.guild.getUserByID(event.message.mentions[0].longID)
        val embed: EmbedBuilder = BotUtils.embed("User Info", event.author)

        embed.setLenient(true)
        embed.withDescription("User info for ${user.mention()}")
        embed.appendField("Name", "@${user.name}#${user.discriminator}", true)
        embed.appendField("ID", user.longID.toString(), true)

        val roles: String = buildString {
            for (role: IRole in user.getRolesForGuild(event.guild)) {
                append(role.name)
                append(", ")
            }

            setLength(length - 2)
        }

        embed.appendField("Roles", roles, false)
        val joinDate: ZonedDateTime = ZonedDateTime.of(user.creationDate, ZoneId.systemDefault())
        embed.appendField("Joined Discord", joinDate.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)), false)

        val nicknames: String = buildString {
            for (guild: IGuild in user.shard.guilds) {
                if (user.getNicknameForGuild(guild) != null) {
                    append(user.getNicknameForGuild(guild))
                    append(", ")
                }
            }

            if(length == 0) {
                append("None")
            } else {
                setLength(length - 2)
            }
        }

        embed.appendField("Known Nicknames", nicknames, false)

        val presence: String = buildString {
            val friendlyText: String = when (user.presence.status) {
                StatusType.ONLINE -> "Online"
                StatusType.IDLE -> "Idle"
                StatusType.DND -> "Do not Disturb"
                StatusType.STREAMING -> "Streaming"
                StatusType.UNKNOWN -> "Unknown"
                StatusType.OFFLINE -> "Offline"
                null -> "Unknown"
            }

            val playingText: String = if (user.presence.status == StatusType.STREAMING) {
                ", Streaming ${user.presence.playingText.get()} at ${user.presence.streamingUrl}"
            } else if(user.presence.status == StatusType.ONLINE || user.presence.status == StatusType.IDLE || user.presence.status == StatusType.DND) {
                if(user.presence.playingText.isPresent) { ", Playing " + user.presence.playingText.get() } else { "" }
            } else {
                ""
            }

            append(friendlyText)
            append(playingText)
        }

        embed.appendField("Presence", presence, false)
        embed.appendField("Is a bot?", if(user.isBot) { "Yes" } else { "No" }, false)

        BotUtils.sendMessage(embed.build(), event.channel)
    }
}