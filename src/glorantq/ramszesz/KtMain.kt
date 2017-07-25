package glorantq.ramszesz

import ch.qos.logback.classic.Level
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val inviteUrl: String = "https://discordapp.com/api/oauth2/authorize?client_id=338377690002489344&scope=bot&permissions=356936710"
val discordToken: String = "MzM4Mzc3NjkwMDAyNDg5MzQ0.DFfL6A.JnDFOSMYwY7UUCwV0erzvb7KFY0"

fun main(args: Array<String>) {
    (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger).level = Level.DEBUG

    Ramszesz.instance
}