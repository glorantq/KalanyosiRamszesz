package glorantq.ramszesz

import ch.qos.logback.classic.Level
import com.cloudinary.Cloudinary
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val inviteUrl: String = "https://discordapp.com/oauth2/authorize?client_id=338377690002489344&permissions=356936710&scope=bot"
val discordToken: String = "MzM4Mzc3NjkwMDAyNDg5MzQ0.DFvU5Q.rZZVphQizdxOV2Rhn7UMnM4mQoU"
val cloudinary: Cloudinary = Cloudinary("cloudinary://655653563659357:qGlhTQyQ4pNmo_Qunx916iiZJ0Q@djg79hteg")

fun main(args: Array<String>) {
    (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger).level = Level.DEBUG

    Ramszesz.instance
}