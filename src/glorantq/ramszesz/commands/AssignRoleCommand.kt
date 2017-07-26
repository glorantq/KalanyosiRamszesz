package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import glorantq.ramszesz.config.ConfigFile
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IRole
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Created by glora on 2017. 07. 23..
 */
class AssignRoleCommand : ICommand {
    override val commandName: String
        get() = "assignrole"
    override val description: String
        get() = "Assign roles to a user"
    override val permission: Permission
        get() = Permission.ADMIN
    override val extendedHelp: String
        get() = "Assign a role to a user. Mention the user and specify the roles"
    override val aliases: List<String>
        get() = listOf("addrole", "ar", "give")
    override val usage: String
        get() = "@Mention Role[,Role2]"

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val embed: EmbedBuilder = BotUtils.embed("Assign Roles", event.author)
        if(!BotUtils.hasPermissions(268435456, event.author, event.guild)) {
            embed.withDescription("You don't have permissions to manage roles!")
            event.channel.sendMessage(embed.build())
            return
        }

        val mentions: List<IUser> = event.message.mentions
        if (mentions.isEmpty()) {
            BotUtils.sendUsageEmbed("You need to mention a user!", "Kick", event.author, event, this)
            return
        }
        val nameLength: Int = mentions[0].name.split(" ").size

        if (args.size == nameLength) {
            BotUtils.sendUsageEmbed("You need to specify the roles!", "Kick", event.author, event, this)
            return
        }

        val builder: StringBuilder = StringBuilder()
        for(part: String in args.subList(nameLength, args.size)) {
            builder.append(part)
            builder.append(" ")
        }

        val roleList: String = builder.toString().trim()
        val roleNames: List<String> = roleList.split(",")

        val assignedRoles: ArrayList<String> = ArrayList()

        for(roleName: String in roleNames) {
            if(roleName.isEmpty() || roleName == " ") continue
            val roles: List<IRole> = event.guild.getRolesByName(roleName.trim())
            if (roles.isEmpty()) {
                continue
            } else {
                try {
                    mentions[0].addRole(roles[0])
                    assignedRoles.add(roleName)
                } catch (e: Exception) {

                }
            }
        }

        val assignedBuilder: StringBuilder = StringBuilder()
        for(i: Int in 0..assignedRoles.size - 1) {
            assignedBuilder.append(assignedRoles[i])
            if(i == (assignedRoles.size - 2)) {
                assignedBuilder.append(" and ")
            } else if(i < assignedRoles.size - 1) {
                assignedBuilder.append(", ")
            }
        }

        if(assignedBuilder.toString().trim().isNotEmpty()) {
            embed.withDescription("Assigned `$assignedBuilder` role(s) to ${mentions[0].mention()}")
            val config: ConfigFile = BotUtils.getGuildConfig(event)
            if(config.logModerations) {
                event.guild.getChannelByID(config.modLogChannel).sendMessage(embed.build())
            }
        } else {
            embed.withDescription("No roles were assigned to ${mentions[0].mention()}")
        }

        event.channel.sendMessage(embed.build())
    }
}