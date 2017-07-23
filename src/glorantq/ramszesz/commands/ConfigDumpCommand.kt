package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import glorantq.ramszesz.config.ConfigFile
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder

/**
 * Created by glorantq on 2017. 07. 23..
 */
class ConfigDumpCommand : Command {
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

        val embedBuilder: EmbedBuilder = EmbedBuilder()
        embedBuilder.withColor(BotUtils.embedColor)
        embedBuilder.withAuthorName("Config dump for ${event.guild.name}")
        embedBuilder.withFooterText("Command ran by @${event.author.name}")
        embedBuilder.withFooterIcon(event.author.avatarURL)
        embedBuilder.withTimestamp(System.currentTimeMillis())

        embedBuilder.appendField("guildId", config.guildId, true)
        embedBuilder.appendField("emojiNameAppend", config.emojiNameAppend.toString(), true)
        embedBuilder.appendField("deleteCommands", config.deleteCommands.toString(), true)
        embedBuilder.appendField("userRole", if(config.userRole != -1L) {event.guild.getRoleByID(config.userRole).name} else {"Everyone"}, true)
        embedBuilder.appendField("adminRole", if(config.adminRole != -1L) {event.guild.getRoleByID(config.adminRole).name} else {"Server Owner"}, true)

        event.channel.sendMessage(embedBuilder.build())
    }
}