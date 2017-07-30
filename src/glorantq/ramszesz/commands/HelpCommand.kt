package glorantq.ramszesz.commands

import glorantq.ramszesz.utils.BotUtils
import glorantq.ramszesz.Ramszesz
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder

/**
 * Created by glorantq on 2017. 07. 22..
 */
class HelpCommand : ICommand {
    override val commandName: String
        get() = "help"
    override val description: String
        get() = "Prints all commands. Specify a command to get more help."
    override val permission: Permission
        get() = Permission.NONE
    override val usage: String
        get() = "[Command]"
    override val availabeInDM: Boolean
        get() = true

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val embedBuilder: EmbedBuilder = BotUtils.embed("ICommand Help", event.author)

        if(args.isEmpty()) {
            Ramszesz.instance.commands
                    .filterNot { it.undocumented }
                    .forEach { embedBuilder.appendField(it.commandName, it.description, false) }

        } else {
            val commandName: String = args[0]
            if(commandName.equals("document-all", true)) {
                Ramszesz.instance.commands
                        .forEach { embedBuilder.appendField(it.commandName, it.description, false) }
            } else {
                val command: ICommand? = Ramszesz.instance.commands.firstOrNull { it.commandName.equals(commandName, true) }

                if (command == null) {
                    embedBuilder.appendField("Invalid ICommand", "The command `$commandName` is invalid. Run `r!help` for a list of commands", false)
                } else {
                    embedBuilder.withAuthorName("Showing help for $commandName")
                    embedBuilder.appendField("Description", command.description, false)
                    embedBuilder.appendField("Usage", "${BotUtils.prefix}${command.commandName} ${command.usage}", false)
                    embedBuilder.appendField("Extra Help", command.extendedHelp, false)
                    embedBuilder.appendField("Does it work in DMs?", if(command.availabeInDM) { "Yes" } else { "No" }, false)

                    if (command.aliases.isNotEmpty()) {
                        val builder: StringBuilder = StringBuilder()
                        for (alias: String in command.aliases) {
                            builder.append(alias)
                            builder.append(", ")
                        }
                        val aliases: String = builder.toString()

                        embedBuilder.appendField("Aliases", aliases.substring(0, aliases.length - 2), false)
                    }

                    embedBuilder.appendField("Permission Level", command.permission.name, false)
                }
            }
        }

        event.author.orCreatePMChannel.sendMessage(embedBuilder.build())
        if(!event.channel.isPrivate) {
            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Help", "Alright ${event.author.mention()}, sent you a DM!", event.author), event.channel)
        }
    }
}