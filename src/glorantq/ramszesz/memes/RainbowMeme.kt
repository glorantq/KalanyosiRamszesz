package glorantq.ramszesz.memes

import com.cloudinary.utils.ObjectUtils
import glorantq.ramszesz.cloudinary
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IUser
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

class RainbowMeme : IMeme {
    override val name: String
        get() = "rainbow"
    override var parameters: ArrayList<MemeParameter> = arrayListOf(MemeParameter(MemeParameter.Companion.Type.USER))

    val random: Random = Random()

    override fun execute(event: MessageReceivedEvent) {
        val user: IUser = parameters[0].value as IUser

        if(user.longID == 334406581695283202) {
            deliverMeme(event, "https://cdn.discordapp.com/attachments/333835915921457152/345653624262623236/gaylogo.png")
            return
        }

        val profileImage: BufferedImage = downloadProfileImage(user)

        val overlay: BufferedImage = ImageIO.read(File("./assets/rainbowOverlay.png"))
        val rainbow: BufferedImage = ImageIO.read(File("./assets/rainbow.png"))

        val combined: BufferedImage = BufferedImage(profileImage.width, profileImage.height, BufferedImage.TYPE_INT_ARGB)
        val graphics: Graphics2D = combined.graphics as Graphics2D

        graphics.drawImage(profileImage, 0, 0, combined.width, combined.height, null)
        graphics.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)
        graphics.drawImage(overlay, 0, 0, combined.width, combined.height, null)
        graphics.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)

        for(i: Int in 1..(Math.max(random.nextInt(15), 10))) {
            val size: Int = ((combined.width / 4) * Math.max(Math.random() * 1.4, 1.2)).toInt()
            val x: Int = random.nextInt(combined.width - size)
            val y: Int = random.nextInt(combined.height - size)
            val angle: Double = random.nextInt(360).toDouble()

            val prevTransform: AffineTransform = graphics.transform
            graphics.rotate(Math.toRadians(angle), x.toDouble(), y.toDouble())
            graphics.drawImage(rainbow, x, y, size, size, null)
            graphics.transform = prevTransform
        }

        graphics.dispose()

        val imageFile: File = File.createTempFile("rainbow-meme-${event.author.name}", ".png")
        imageFile.deleteOnExit()
        ImageIO.write(combined, "png", imageFile)

        val url: String = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap())["secure_url"].toString()

        deliverMeme(event, url)
        imageFile.delete()
    }
}