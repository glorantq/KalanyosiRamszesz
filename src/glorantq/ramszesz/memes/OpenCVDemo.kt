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
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ColorConvertOp
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import javax.imageio.ImageIO


class OpenCVDemo : IMeme {
    override val name: String
        get() = "opencv"
    override var parameters: ArrayList<MemeParameter> = arrayListOf(MemeParameter(MemeParameter.Companion.Type.STRING))

    override fun execute(event: MessageReceivedEvent) {
        var image: BufferedImage = ImageIO.read(URL(parameters[0].value.toString()))
        val op = ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB), null)
        image = op.filter(image, image)

        val faceCascade = CascadeClassifier()
        val eyeCascade = CascadeClassifier()
        val profileCascade = CascadeClassifier()
        val loaded = faceCascade.load(File("./classifiers/haarcascades/haarcascade_frontalface_alt.xml").absolutePath) &&
                     eyeCascade.load("./classifiers/haarcascades/haarcascade_eye.xml") &&
                     profileCascade.load("./classifiers/haarcascades/haarcascade_profileface.xml")

        if(!loaded) {
            BotUtils.sendMessage("Failed to load cascade!", event.channel)
            return
        }

        val mat = bufferedImageToMat(image)
        val greyFrame = Mat()

        Imgproc.cvtColor(mat, greyFrame, Imgproc.COLOR_BGR2GRAY)
        Imgproc.equalizeHist(greyFrame, greyFrame)

        val height: Int = greyFrame.height()
        val absoluteFaceHeight: Double = Math.round(height * 0.1).toDouble()

        val faces = MatOfRect()
        faceCascade.detectMultiScale(greyFrame, faces, 1.1, 2, 0 or Objdetect.CASCADE_SCALE_IMAGE, Size(absoluteFaceHeight, absoluteFaceHeight), Size())

        val profileFaces = MatOfRect()
        profileCascade.detectMultiScale(greyFrame, profileFaces, 1.1, 2, 0 or Objdetect.CASCADE_SCALE_IMAGE, Size(absoluteFaceHeight, absoluteFaceHeight), Size())

        val faceArray: Array<Rect> = faces.toArray()
        val profileArray: Array<Rect> = profileFaces.toArray()

        val g: Graphics2D = image.graphics as Graphics2D
        g.stroke = BasicStroke(5f)
        for(rect: Rect in faceArray) {
            val eyes = MatOfRect()
            val faceMat = Mat(greyFrame, rect)
            val featureSize: Double = Math.round(faceMat.height() * 0.21).toDouble()
            eyeCascade.detectMultiScale(faceMat, eyes, 1.1, 2, 0 or Objdetect.CASCADE_SCALE_IMAGE, Size(featureSize, featureSize), Size())
            g.color = Color.RED
            for(eyeRect: Rect in eyes.toArray()) {
                g.drawRect(eyeRect.x + rect.x, eyeRect.y + rect.y, eyeRect.width, eyeRect.height)
            }
            g.color = Color.GREEN
            g.drawRect(rect.x, rect.y, rect.width, rect.height)
        }

        for(rect: Rect in profileArray) {
            val near = faceArray.any { Math.abs(rect.x - it.x) <= greyFrame.height() * 0.1 || Math.abs(rect.y - it.y) <= greyFrame.height() * 0.1 }
            if(near) {
                continue
            }

            val faceMat = Mat(greyFrame, rect)
            val eyes = MatOfRect()
            val featureSize: Double = Math.round(faceMat.height() * 0.21).toDouble()
            eyeCascade.detectMultiScale(faceMat, eyes, 1.1, 2, 0 or Objdetect.CASCADE_SCALE_IMAGE, Size(featureSize, featureSize), Size())
            g.color = Color.RED
            for(eyeRect: Rect in eyes.toArray()) {
                g.drawRect(eyeRect.x + rect.x, eyeRect.y + rect.y, eyeRect.width, eyeRect.height)
            }
            g.color = Color.CYAN
            g.drawRect(rect.x, rect.y, rect.width, rect.height)
        }
        g.dispose()

        val imageFile: File = File.createTempFile("opencv-test-${event.author.name}", ".png")
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