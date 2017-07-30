package glorantq.ramszesz.memes

import glorantq.ramszesz.utils.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder

/**
 * Created by glora on 2017. 07. 26..
 */
interface IMeme {
    val name: String

    var parameters: ArrayList<MemeParameter>

    fun execute(event: MessageReceivedEvent)

    fun deliverMeme(event: MessageReceivedEvent, url: String) {
        val builder: EmbedBuilder = BotUtils.embed("Meme Generator", event.author)
        builder.withDescription("Her'es your m'eme")
        builder.withImage(url)
        builder.withUrl(url)

        BotUtils.sendMessage(builder.build(), event.channel)
    }
}