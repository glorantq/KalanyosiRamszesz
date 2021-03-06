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
 * Created by glora on 2017. 07. 28..
 */
class EmojiMovieMeme : IMeme {
    override val name: String
        get() = "emojimovie"
    override var parameters: ArrayList<MemeParameter> = arrayListOf(MemeParameter(MemeParameter.Companion.Type.USER))

    override fun execute(event: MessageReceivedEvent) {
        val user: IUser = parameters[0].value as IUser
        val urlConnection: URLConnection = URL(user.avatarURL.replace("webp", "png")).openConnection()
        urlConnection.setRequestProperty("User-Agent", "Kalányosi Ramszesz/1.0")
        urlConnection.connect()
        println(urlConnection.url)
        val profileImage: BufferedImage = ImageIO.read(urlConnection.getInputStream())

        val emojiLarge: BufferedImage = ImageIO.read(File("./assets/emoji_big.png"))
        val emojiSmall: BufferedImage = ImageIO.read(File("./assets/emoji_small.png"))
        val emojiLean: BufferedImage = ImageIO.read(File("./assets/emoji_lean.png"))

        val ratio: Float = (emojiLarge.width.toFloat() / emojiLarge.height.toFloat())
        val profileImageSize: Int = profileImage.width * 2
        val logoHeight: Int =  (profileImageSize / ratio).toInt()
        val combined: BufferedImage = BufferedImage(profileImageSize, profileImageSize, BufferedImage.TYPE_INT_ARGB)
        val smallSize: Int = profileImageSize / 4

        val graphics: Graphics = combined.graphics
        graphics.drawImage(profileImage, 0, 0, profileImageSize, profileImageSize, null)
        graphics.drawImage(emojiLarge, 0, 0, profileImageSize, logoHeight, null)
        graphics.drawImage(emojiSmall, 0, profileImageSize - smallSize, smallSize, smallSize, null)
        graphics.drawImage(emojiLean, profileImageSize - smallSize, profileImageSize - smallSize, smallSize, smallSize, null)
        graphics.dispose()

        val imageFile: File = File.createTempFile("emoji-movie-meme-${event.author.name}", ".png")
        imageFile.deleteOnExit()
        ImageIO.write(combined, "png", imageFile)

        val url: String = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap())["secure_url"].toString()

        deliverMeme(event, url)
        imageFile.delete()
    }
}