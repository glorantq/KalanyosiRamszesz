package glorantq.ramszesz.memes

import glorantq.ramszesz.utils.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import java.awt.image.BufferedImage
import java.net.URL
import java.net.URLConnection
import javax.imageio.ImageIO

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

    fun downloadProfileImage(user: IUser): BufferedImage {
        val urlConnection: URLConnection = URL("${user.avatarURL.replace("webp", "png")}?size=2048").openConnection()
        urlConnection.setRequestProperty("User-Agent", "Kalányosi Ramszesz/1.0")
        urlConnection.connect()
        return ImageIO.read(urlConnection.getInputStream())
    }
}