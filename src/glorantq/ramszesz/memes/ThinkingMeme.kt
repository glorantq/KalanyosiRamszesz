package glorantq.ramszesz.memes

import com.cloudinary.utils.ObjectUtils
import glorantq.ramszesz.cloudinary
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IUser
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.net.URLConnection
import javax.imageio.ImageIO

/**
 * Created by glora on 2017. 07. 27..
 */
class ThinkingMeme : IMeme {
    override val name: String
        get() = "thinking"
    override var parameters: ArrayList<MemeParameter> = arrayListOf(MemeParameter(MemeParameter.Companion.Type.USER))

    override fun execute(event: MessageReceivedEvent) {
        val user: IUser = parameters[0].value as IUser
        val urlConnection: URLConnection = URL(user.avatarURL.replace("webp", "png")).openConnection()
        urlConnection.setRequestProperty("User-Agent", "Kalányosi Ramszesz/1.0")
        urlConnection.connect()
        println(urlConnection.url)
        val profileImage: BufferedImage = ImageIO.read(urlConnection.getInputStream())

        val thinking: BufferedImage = ImageIO.read(File("./assets/thinking.png"))
        val combined: BufferedImage = BufferedImage(profileImage.width, profileImage.height, BufferedImage.TYPE_INT_ARGB)

        val graphics: Graphics = combined.graphics
        graphics.drawImage(profileImage, 0, 0, null)
        val thinkHeight: Int = profileImage.height / 2
        val thinkWidth: Int = profileImage.width / 2
        graphics.drawImage(thinking, thinkWidth / 4, profileImage.height - thinkHeight - (thinkHeight / 4), thinkWidth, thinkHeight, null)
        graphics.dispose()

        val imageFile: File = File.createTempFile("thinking-meme-${event.author.name}", ".png")
        imageFile.deleteOnExit()
        ImageIO.write(combined, "png", imageFile)

        val url: String = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap())["secure_url"].toString()

        deliverMeme(event, url)
        imageFile.delete()
    }
}