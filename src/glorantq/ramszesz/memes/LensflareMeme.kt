package glorantq.ramszesz.memes

import com.cloudinary.utils.ObjectUtils
import glorantq.ramszesz.cloudinary
import glorantq.ramszesz.utils.BotUtils
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import org.opencv.objdetect.Objdetect
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.impl.obj.User
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO


class LensflareMeme : IMeme {
    override val name: String
        get() = "lensflare"

    override var parameters: ArrayList<MemeParameter> = arrayListOf(MemeParameter(MemeParameter.Companion.Type.USER))

    override fun execute(event: MessageReceivedEvent) {
        val image: BufferedImage = downloadProfileImage(parameters[0].value as User)
        val lensflare: BufferedImage = ImageIO.read(File("./assets/lensflare.png"))
        val ascended: BufferedImage = ImageIO.read(File("./assets/ascended.png"))

        val ratio: Float = (lensflare.width.toFloat() / lensflare.height.toFloat())
        val lensflareHeight: Int =  (image.height / ratio).toInt()
        val scaledLensflare = BufferedImage(image.width, lensflareHeight, BufferedImage.TYPE_INT_ARGB)
        val lensflareG: Graphics2D = scaledLensflare.graphics as Graphics2D
        lensflareG.drawImage(lensflare, 0, 0, scaledLensflare.width, scaledLensflare.height, null)
        lensflareG.dispose()

        val aRatio: Float = (ascended.width.toFloat() / ascended.height.toFloat())
        val ascendedHeight: Int =  (image.height / aRatio).toInt()
        val scaledAscended = BufferedImage(image.width, ascendedHeight, BufferedImage.TYPE_INT_ARGB)
        val ascendedG: Graphics2D = scaledAscended.graphics as Graphics2D
        ascendedG.drawImage(ascended, 0, 0, scaledAscended.width, scaledAscended.height, null)
        ascendedG.dispose()

        val faceCascade = CascadeClassifier()
        val eyeCascade = CascadeClassifier()
        val loaded = faceCascade.load(File("./classifiers/haarcascades/haarcascade_frontalface_alt.xml").absolutePath) &&
                eyeCascade.load("./classifiers/haarcascades/haarcascade_eye.xml")

        if(!loaded) {
            BotUtils.sendMessage("Failed to load cascade!", event.channel)
            return
        }

        val mat = bufferedImageToMat(image)
        val greyFrame = Mat()

        Imgproc.cvtColor(mat, greyFrame, Imgproc.COLOR_BGR2GRAY)
        Imgproc.equalizeHist(greyFrame, greyFrame)

        val height: Int = greyFrame.height()
        val absoluteFaceHeight: Double = Math.round(height * 0.3).toDouble()

        val faces = MatOfRect()
        faceCascade.detectMultiScale(greyFrame, faces, 1.1, 2, 0 or Objdetect.CASCADE_SCALE_IMAGE, Size(absoluteFaceHeight, absoluteFaceHeight), Size())

        val faceArray: Array<Rect> = faces.toArray()
        var eyeArray: Array<Rect>? = null
        var faceRect: Rect? = null

        for(rect: Rect in faceArray) {
            val eyes = MatOfRect()
            val faceMat = Mat(greyFrame, rect)
            val featureSize: Double = Math.round(faceMat.height() * 0.21).toDouble()
            eyeCascade.detectMultiScale(faceMat, eyes, 1.1, 2, 0 or Objdetect.CASCADE_SCALE_IMAGE, Size(featureSize, featureSize), Size())
            if(eyes.toArray().size >= 2) {
                eyeArray = eyes.toArray()
                faceRect = rect
                break
            }
        }

        if(eyeArray == null || faceRect == null) {
            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Meme Generator", "No face found in the given image", event.author), event.channel)
            return
        }

        if(eyeArray.size < 2) {
            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Meme Generator", "No eyes found in the given image!", event.author), event.channel)
            return
        }

        val g2d: Graphics2D = image.graphics as Graphics2D
        for(i: Int in 0 until 2) {
            val rect: Rect = eyeArray[i]
            val lensflareX: Int = faceRect.x + rect.x - scaledLensflare.width / 2 + rect.width / 2
            val lensflareY: Int = faceRect.y + rect.y - scaledLensflare.height / 2 + rect.height / 2

            g2d.drawImage(scaledLensflare, lensflareX, lensflareY,null)
        }

        for(x: Int in 0 until image.width) {
            for(y: Int in 0 until image.height) {
                val pixel = image.getRGB(x, y)
                val r = pixel shr 16 and 0xFF
                val g = pixel shr 8 and 0xFF
                val b = pixel and 0xFF

                val hsb: FloatArray = Color.RGBtoHSB(r, g, b, null)

                image.setRGB(x, y, Color.HSBtoRGB(hsb[0], 240f, hsb[2]))
            }
        }

        val oneEye: Rect = eyeArray[0]
        if(oneEye.y > image.height / 2) {
            g2d.drawImage(scaledAscended, image.width / 2 - scaledAscended.width / 2, 10, image.width, scaledAscended.height, null)
        } else {
            g2d.drawImage(scaledAscended, image.width / 2 - scaledAscended.width / 2, image.height - scaledAscended.height - 10, image.width, scaledAscended.height, null)
        }

        g2d.dispose()

        val imageFile: File = File.createTempFile("lensflare-meme-${event.author.name}", ".png")
        imageFile.deleteOnExit()
        ImageIO.write(image, "png", imageFile)

        val url: String = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap())["secure_url"].toString()

        deliverMeme(event, url)
        imageFile.delete()
    }

    private fun bufferedImageToMat(image: BufferedImage): Mat {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", byteArrayOutputStream)
        byteArrayOutputStream.flush()
        return Imgcodecs.imdecode(MatOfByte(*byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED)
    }
}