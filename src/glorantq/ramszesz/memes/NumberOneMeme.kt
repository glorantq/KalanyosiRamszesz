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
 * Created by glora on 2017. 07. 26..
 */
class NumberOneMeme : IMeme {
    override val name: String
        get() = "numberone"
    override var parameters: ArrayList<MemeParameter> = arrayListOf(
            MemeParameter(MemeParameter.Companion.Type.USER),
            MemeParameter(MemeParameter.Companion.Type.USER),
            MemeParameter(MemeParameter.Companion.Type.USER),
            MemeParameter(MemeParameter.Companion.Type.USER)
    )

    override fun execute(event: MessageReceivedEvent) {
        val pictures: ArrayList<BufferedImage> = arrayListOf()

        for(i: Int in 0..3) {
            val user: IUser = parameters[i].value as IUser
            val urlConnection: URLConnection = URL(user.avatarURL.replace("webp", "png")).openConnection()
            urlConnection.setRequestProperty("User-Agent", "Kalányosi Ramszesz/1.0")
            urlConnection.connect()
            println(urlConnection.url)
            pictures.add(ImageIO.read(urlConnection.getInputStream()))
        }

        val background: BufferedImage = ImageIO.read(File("./assets/numberone.jpg"))
        val combined: BufferedImage = BufferedImage(background.width, background.height, BufferedImage.TYPE_INT_RGB)

        val graphics: Graphics = combined.graphics
        graphics.drawImage(background, 0, 0, null)

        graphics.drawImage(pictures[0], 205, 445 - 260, 260, 260, null)
        graphics.drawImage(pictures[1], 575, 580 - 260, 260, 260, null)
        graphics.drawImage(pictures[2], 780, 380 - 215, 215, 215, null)
        graphics.drawImage(pictures[3], 555, 260 - 240, 240, 240, null)

        graphics.dispose()

        val final: BufferedImage = BufferedImage(640, 360, BufferedImage.TYPE_INT_RGB)

        val finalG: Graphics = final.graphics
        finalG.drawImage(combined, 0, 0, 640, 360, null)
        finalG.dispose()

        val imageFile: File = File.createTempFile("numberone-meme-${event.author.name}", ".png")
        imageFile.deleteOnExit()
        ImageIO.write(final, "png", imageFile)

        val url: String = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap())["secure_url"].toString()

        deliverMeme(event, url)
        imageFile.delete()
    }
}