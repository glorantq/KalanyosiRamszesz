package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder

/**
 * Created by glora on 2017. 07. 28..
 */
class GuildListCommand : ICommand {
    override val commandName: String
        get() = "guildlist"
    override val description: String
        get() = "List the guilds this bot is in"
    override val permission: Permission
        get() = Permission.USER
    override val aliases: List<String>
        get() = listOf("guilds")
    override val availabeInDM: Boolean
        get() = true

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val embed: EmbedBuilder = BotUtils.embed("Guild List", event.author)
        embed.withDescription("This bot is in ${event.client.guilds.size} guilds")
        val guilds: String = buildString {
            event.client.guilds.forEach {
                append(it.name)
                append("\n")
            }
        }
        embed.appendField("Guilds", guilds, false)

        event.author.orCreatePMChannel.sendMessage(embed.build())
        if(!event.channel.isPrivate) {
            event.channel.sendMessage(BotUtils.createSimpleEmbed("Guild List", "Alright ${event.author.mention()}, sent you a DM!", event.author))
        }
    }
}