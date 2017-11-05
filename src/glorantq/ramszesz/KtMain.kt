package glorantq.ramszesz

import ch.qos.logback.classic.Level
import com.cloudinary.Cloudinary
import org.opencv.core.Core
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val inviteUrl: String = "https://discordapp.com/oauth2/authorize?client_id=338377690002489344&permissions=356936710&scope=bot"
val discordToken: String = System.getenv("DISCORD_TOKEN")
val cloudinary: Cloudinary = Cloudinary()
val osuKey: String = System.getenv("OSU_TOKEN")

fun main(args: Array<String>) {
    (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger).level = Level.DEBUG

    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    Ramszesz.instance
}