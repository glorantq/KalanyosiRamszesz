package glorantq.ramszesz

import ch.qos.logback.classic.Level
import com.cloudinary.Cloudinary
import org.opencv.core.Core
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val inviteUrl: String = "https://discordapp.com/oauth2/authorize?client_id=338377690002489344&permissions=356936710&scope=bot"
val discordToken: String = "MzM4Mzc3NjkwMDAyNDg5MzQ0.DG-R2g.ZHDE3V97i3gox6mKj2uCKkw2au4"
val cloudinary: Cloudinary = Cloudinary("cloudinary://145951775697273:q1tzeZ7pq-6CK49Wkk8rQ9BZZ6c@djg79hteg")
val osuKey: String = "4de60cac51785486065855bdaebca5a52ebe2923"

fun main(args: Array<String>) {
    (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger).level = Level.DEBUG

    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    Ramszesz.instance
}