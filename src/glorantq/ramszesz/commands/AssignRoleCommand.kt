package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IRole
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.MissingPermissionsException

/**
 * Created by glora on 2017. 07. 23..
 */
class AssignRoleCommand : Command {
    override val commandName: String
        get() = "assignrole"
    override val description: String
        get() = "Assign a role to a user"
    override val permission: Permission
        get() = Permission.ADMIN
    override val extendedHelp: String
        get() = "Assign a role to a user. Mention the user and specify the role"
    override val aliases: List<String>
        get() = listOf("addrole", "ar", "give")

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val embed: EmbedBuilder = BotUtils.embed("Assign Role", event.author)
        val hasPerms: Boolean = event.author.getPermissionsForGuild(event.guild).any { it.hasPermission(268435456) }
        if(!hasPerms) {
            embed.withDescription("You don't have permissions to manage roles!")
            event.channel.sendMessage(embed.build())
            return
        }

        val mentions: List<IUser> = event.message.mentions
        if (mentions.isEmpty()) {
            embed.withDescription("You need to mention a user!")
            embed.appendField("Usage", "`r!assignrole @Mention Role`", false)
            event.channel.sendMessage(embed.build())
            return
        }
        val nameLength: Int = mentions[0].name.split(" ").size

        if (args.size == nameLength) {
            embed.withDescription("You need to specify a role!")
            embed.appendField("Usage", "`r!assignrole @Mention Role`", false)
            event.channel.sendMessage(embed.build())
            return
        }

        val builder: StringBuilder = StringBuilder()
        for(part: String in args.subList(nameLength, args.size)) {
            builder.append(part)
            builder.append(" ")
        }

        val roles: List<IRole> = event.guild.getRolesByName(builder.toString().trim())
        if(roles.isEmpty()) {
            embed.withDescription("The role `${builder.toString().trim()}` is invalid")
        } else {
            try {
                mentions[0].addRole(roles[0])
                embed.withDescription("Successfully assigned the `${roles[0].name}` role to ${mentions[0].mention()}")
            } catch (e: Exception) {
                embed.withDescription("Failed to assign role!")
                embed.appendField(e::class.simpleName, e.message, false)
            }
        }

        event.channel.sendMessage(embed.build())
    }
}