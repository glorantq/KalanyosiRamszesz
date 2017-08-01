package glorantq.ramszesz.utils

import glorantq.ramszesz.Ramszesz
import glorantq.ramszesz.commands.ICommand
import glorantq.ramszesz.commands.Permission
import glorantq.ramszesz.config.ConfigFile
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.internal.json.objects.EmbedObject
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer

/**
 * Created by glorantq on 2017. 07. 22..
 */
class BotUtils {
    companion object {
        val prefix: String = "r!"
        val embedColor: Int = 0xFF8F00

        fun buildDiscordClient(token: String): IDiscordClient {
            return ClientBuilder().withToken(token).withRecommendedShardCount().build()
        }

        fun getGuildConfig(event: MessageReceivedEvent): ConfigFile {
            return getGuildConfig(event.guild.stringID)
        }

        fun getGuildConfig(id: String): ConfigFile {
            return Ramszesz.instance.getConfigForGuild(id)
        }

        fun isAuthorUser(event: MessageReceivedEvent): Boolean {
           return isUser(event.author, event.guild)
        }

        fun isUser(user: IUser, guild: IGuild): Boolean {
            val config: ConfigFile = getGuildConfig(guild.stringID)

            if(config.userRole == -1L) return true

            user.getRolesForGuild(guild)
                    .filter { it.longID == config.userRole }
                    .forEach { return true }

            return false
        }

        fun isAuthorAdmin(event: MessageReceivedEvent): Boolean {
            return isUserAdmin(event.author, event.guild)
        }

        fun isUserAdmin(user: IUser, guild: IGuild): Boolean {
            val config: ConfigFile = getGuildConfig(guild.stringID)

            if(guild.ownerLongID == user.longID) return true
            if(config.adminRole == -1L) return false

            user.getRolesForGuild(guild)
                    .filter { it.longID == config.adminRole }
                    .forEach { return true }

            return false
        }

        fun getPermissionLevel(user: IUser, guild: IGuild?): Permission {
            return if(guild == null || guild.ownerLongID == user.longID) {
                Permission.OWNER
            } else if(isUserAdmin(user, guild)) {
                Permission.ADMIN
            } else if(isUser(user, guild)) {
                Permission.USER
            } else {
                Permission.NONE
            }
        }

        fun getSpecialPermissions(user: IUser): List<Permission> {
            val permissions: ArrayList<Permission> = ArrayList()

            if(Ramszesz.instance.squad.contains(user.longID)) {
                permissions.add(Permission.SQUAD)
            }

            if(user.longID == 251374678688530433) {
                permissions.add(Permission.BOT_OWNER)
            }

            return permissions
        }

        fun createSimpleEmbed(header: String, content: String, author: IUser): EmbedObject {
            return embed(header, author).withDescription(content).build()
        }

        fun embed(header: String, author: IUser): EmbedBuilder {
            val builder: EmbedBuilder = EmbedBuilder()
            builder.withColor(embedColor)
            builder.withAuthorName(header)
            builder.withFooterText("Command ran by @${author.name}")
            builder.withFooterIcon(author.avatarURL)
            builder.withTimestamp(System.currentTimeMillis())
            return builder
        }

        fun hasPermissions(permissionNumber: Int, user: IUser, guild: IGuild): Boolean {
            return user.getPermissionsForGuild(guild).any { it.hasPermission(permissionNumber) }
        }

        fun sendUsageEmbed(extraMessage: String, header: String, author: IUser, event: MessageReceivedEvent, command: ICommand) {
            val embed: EmbedBuilder = embed(header, author)
            embed.withDescription(extraMessage)
            embed.appendField("Usage", "${prefix}${command.commandName} ${command.usage}", false)
            sendMessage(embed.build(), event.channel)
        }

        fun sendMessage(message: EmbedObject, channel: IChannel) {
            RequestBuffer.request {
                channel.sendMessage(message)
            }
        }

        fun sendMessage(message: String, channel: IChannel) {
            RequestBuffer.request {
                channel.sendMessage(message)
            }
        }
    }
}