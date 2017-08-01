package glorantq.ramszesz.commands

import glorantq.ramszesz.utils.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder

class TestEmbedCommand : ICommand {
    override val commandName: String
        get() = "testembed"
    override val description: String
        get() = "Send a test embed"
    override val permission: Permission
        get() = Permission.NONE
    override val undocumented: Boolean
        get() = true
    override val availableInDM: Boolean
        get() = true

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val embed: EmbedBuilder = BotUtils.embed("Test Embed", event.author)
        embed.withDescription("This is a test embed!")
        embed.appendField("Arguments", args.toString(), false)
        BotUtils.sendMessage(embed.build(), event.channel)
    }
}