package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import glorantq.ramszesz.config.ConfigFile
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder

/**
 * Created by glorantq on 2017. 07. 23..
 */
class ConfigDumpCommand : ICommand {
    override val commandName: String
        get() = "configdump"
    override val description: String
        get() = "Dumps the config of this guild"
    override val undocumented: Boolean
        get() = true
    override val permission: Permission
        get() = Permission.ADMIN

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val config: ConfigFile = BotUtils.getGuildConfig(event)

        val embedBuilder: EmbedBuilder = BotUtils.embed("Config dump for ${event.guild.name}", event.author)

        embedBuilder.appendField("guildId", config.guildId, true)
        embedBuilder.appendField("emojiNameAppend", config.emojiNameAppend.toString(), true)
        embedBuilder.appendField("deleteCommands", config.deleteCommands.toString(), true)
        embedBuilder.appendField("userRole", if(config.userRole != -1L) {event.guild.getRoleByID(config.userRole).name} else {"Everyone"}, true)
        embedBuilder.appendField("adminRole", if(config.adminRole != -1L) {event.guild.getRoleByID(config.adminRole).name} else {"Server Owner"}, true)
        embedBuilder.appendField("modLogChannel", if(config.modLogChannel != -1L) {event.guild.getChannelByID(config.modLogChannel).name} else {"None"}, true)
        embedBuilder.appendField("logModerations", config.logModerations.toString(), true)

        BotUtils.sendMessage(embedBuilder.build(), event.channel)
    }
}