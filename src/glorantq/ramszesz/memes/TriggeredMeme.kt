package glorantq.ramszesz.memes

import com.cloudinary.utils.ObjectUtils
import glorantq.ramszesz.BotUtils
import glorantq.ramszesz.cloudinary
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.net.URLConnection
import javax.imageio.ImageIO

/**
 * Created by glora on 2017. 07. 26..
 */
class TriggeredMeme : IMeme {
    override val name: String
        get() = "triggered"
    override var parameters: ArrayList<MemeParameter> = arrayListOf(MemeParameter(MemeParameter.Companion.Type.USER))

    override fun execute(event: MessageReceivedEvent) {
        val user: IUser = parameters[0].value as IUser
        val urlConnection: URLConnection = URL(user.avatarURL.replace("webp", "png")).openConnection()
        urlConnection.setRequestProperty("User-Agent", "Kalányosi Ramszesz/1.0")
        urlConnection.connect()
        println(urlConnection.url)
        val profileImage: BufferedImage = ImageIO.read(urlConnection.getInputStream())
        val triggered: BufferedImage = ImageIO.read(File("./assets/triggered.jpg"))

        val ratio: Float = (triggered.width.toFloat() / triggered.height.toFloat())
        val triggeredHeight: Int =  (profileImage.height / ratio).toInt()
        val combined: BufferedImage = BufferedImage(profileImage.width, profileImage.height, BufferedImage.TYPE_INT_RGB)

        val graphics: Graphics = combined.graphics
        graphics.drawImage(profileImage, 0, 0, null)
        graphics.drawImage(triggered, 0, profileImage.height - triggeredHeight, profileImage.width, triggeredHeight, null)
        graphics.drawImage(triggered, 0, 0, profileImage.width, triggeredHeight, null)
        graphics.dispose()

        val imageFile: File = File.createTempFile("triggered-meme-${event.author.name}", ".png")
        imageFile.deleteOnExit()
        ImageIO.write(combined, "png", imageFile)

        val url: String = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap())["secure_url"].toString()

        val builder: EmbedBuilder = BotUtils.embed("Meme Generator", event.author)
        builder.withDescription("Her'es your m'eme")
        builder.withImage(url)

        event.channel.sendMessage(builder.build())
        imageFile.delete()
    }
}