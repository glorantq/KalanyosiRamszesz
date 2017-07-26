package glorantq.ramszesz.memes

import glorantq.ramszesz.BotUtils
import glorantq.ramszesz.commands.MemeCommand
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder

/**
 * Created by glora on 2017. 07. 27..
 */
class MemeList : IMeme {
    override val name: String
        get() = "list"
    override var parameters: ArrayList<MemeParameter> = arrayListOf()

    override fun execute(event: MessageReceivedEvent) {
        val embed: EmbedBuilder = BotUtils.embed("Memes", event.author)
        embed.withDescription("Her'es your list of memes with NO VEGETALS as you ordered sir")

        for(meme: IMeme in MemeCommand.memes) {
            embed.appendField(meme.name, "Takes `${meme.parameters.size}` parameters", false)
        }

        event.channel.sendMessage(embed.build())
    }
}