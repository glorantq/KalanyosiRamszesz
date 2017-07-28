package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IRole

/**
 * Created by glora on 2017. 07. 24..
 */
class RoleIDCommand : ICommand {
    override val commandName: String
        get() = "roleid"
    override val description: String
        get() = "Gets the ID of a role"
    override val permission: Permission
        get() = Permission.USER
    override val extendedHelp: String
        get() = "Get the ID of a role. It doesn't have to be mentionable"
    override val usage: String
        get() = "Role"
    override val undocumented: Boolean
        get() = true

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        if(args.isEmpty()) {
            BotUtils.sendUsageEmbed("You need to specify a role!", "Role ID", event.author, event, this)
            return
        }

        val roles: List<IRole> = event.guild.getRolesByName(args[0])
        if(roles.isEmpty()) {
            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Role ID", "The role `${args[0]}` is invalid", event.author), event.channel)
        } else {
            val role: IRole = roles[0]
            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Role ID", "The ID of `${role.name}` is `${role.longID}`", event.author), event.channel)
        }
    }
}