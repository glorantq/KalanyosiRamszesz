package glorantq.ramszesz

import ch.qos.logback.classic.Level
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val inviteUrl: String = "https://discordapp.com/api/oauth2/authorize?client_id=338377690002489344&scope=bot&permissions=356936710"
val discordToken: String = "MzM4Mzc3NjkwMDAyNDg5MzQ0.DFX8zQ._p2A1-rG1bG5saJLSwT_HoPQQF8"
val grabzitKey: String = "MWIxOWIzYjkwYTNlNGYyNzk5MDRmYTM5MTI2ODI1NTA=Application Secret"
val grabzitSecret: String = "GD9jPyc/Pz8/cmdoPz8/Pz9rYj8/GhNscz8/RT92Ggg="

fun main(args: Array<String>) {
    (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger).level = Level.DEBUG

    Ramszesz.instance
}