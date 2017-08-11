package glorantq.ramszesz

import ch.qos.logback.classic.Level
import com.cloudinary.Cloudinary
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val inviteUrl: String = "https://discordapp.com/oauth2/authorize?client_id=338377690002489344&permissions=356936710&scope=bot"
val discordToken: String = "MzM4Mzc3NjkwMDAyNDg5MzQ0.DG5UYQ.gquDATIxIMbhKI-VWhRBDToA7Us"
val cloudinary: Cloudinary = Cloudinary("cloudinary://671972624247232:ob4XblXXPOLAHrrJUhtTKpYI9zU@djg79hteg")

fun main(args: Array<String>) {
    (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger).level = Level.DEBUG

    Ramszesz.instance
}