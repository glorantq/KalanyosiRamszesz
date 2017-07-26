package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IRole
import java.awt.Color

class ColourCommand : ICommand {
    override val commandName: String
        get() = "rolecolour"
    override val description: String
        get() = "Change the colour of a role"
    override val permission: Permission
        get() = Permission.ADMIN
    override val extendedHelp: String
        get() = "Use this command to set a role's colour to any hexadecimal value"
    override val aliases: List<String>
        get() = listOf("rolecolor", "colour", "color")
    override val usage: String
        get() = "<Colour Code> Role"

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        if(!BotUtils.hasPermissions(268435456, event.author, event.guild)) {
            event.channel.sendMessage(BotUtils.createSimpleEmbed("Colour", "You don't have permissions to manage roles!", event.author))
            return
        }

        if(args.size < 2) {
            BotUtils.sendUsageEmbed("You need to specify both a colour code and a role!", "Colour", event.author, event, this)
            return
        }

        val colourCode: String = args[0].replace("#", "")
        if(colourCode.length != 6) {
            event.channel.sendMessage(BotUtils.createSimpleEmbed("Colour", "The colour code `$colourCode` is invalid!", event.author))
            return
        }

        val roleBuilder: StringBuilder = StringBuilder()
        for(part: String in args.subList(1, args.size)) {
            roleBuilder.append(part)
            roleBuilder.append(" ")
        }

        val roleName: String = roleBuilder.toString().trim()
        val roles: List<IRole> = event.guild.getRolesByName(roleName)
        if(roles.isEmpty()) {
            event.channel.sendMessage(BotUtils.createSimpleEmbed("Colour", "The role `$roleName` is invalid!", event.author))
            return
        }

        val role: IRole = roles[0]
        try {
            role.changeColor(Color(Integer.parseInt(colourCode, 16)))
            event.channel.sendMessage(BotUtils.createSimpleEmbed("Colour", "The colour of `$roleName` has been set to `$colourCode`", event.author))
        } catch(e: NumberFormatException) {
            event.channel.sendMessage(BotUtils.createSimpleEmbed("Colour", "The colour code `$colourCode` is invalid!", event.author))
        }
    }
}