package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by glora on 2017. 07. 28..
 */
class PasswordCommand : ICommand {
    override val commandName: String
        get() = "passwordgen"
    override val description: String
        get() = "Generate a password"
    override val permission: Permission
        get() = Permission.USER
    override val extendedHelp: String
        get() = "Generate a password of random characters and have the bot DM it to you"
    override val aliases: List<String>
        get() = listOf("pass", "passgen", "pwgen")
    override val usage: String
        get() = "[Length]"
    override val availabeInDM: Boolean
        get() = true

    val characters: CharArray = charArrayOf(
            'q', 'w', 'e', 'r', 't', 'z', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'y', 'x', 'c', 'v', 'b', 'n', 'm',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '#', '$', '!', '%', '/'
    )

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        thread(name = "PwGen-${event.author.name}-${System.nanoTime()}", isDaemon = true, start = true) {
            var length: Int
            if (args.isEmpty()) {
                length = 16
            } else {
                try {
                    length = args[0].toInt()
                } catch (e: NumberFormatException) {
                    BotUtils.sendMessage(BotUtils.createSimpleEmbed("Password Generator", "`${args[0]}` is not a valid number", event.author), event.channel)
                    return@thread
                }
            }

            if(length == 0) {
                BotUtils.sendMessage(BotUtils.createSimpleEmbed("Password Generator", "A zero-length password isn't that secure!", event.author), event.channel)
                return@thread
            }

            if(length < 0) {
                length *= -1
                BotUtils.sendMessage(BotUtils.createSimpleEmbed("Password Generator", "The number `${args[0]}` is negative, it has been changed to `$length`", event.author), event.channel)
            }

            if(length > 64) {
                BotUtils.sendMessage(BotUtils.createSimpleEmbed("Password Generator", "The number `${args[0]}` is too large", event.author), event.channel)
                return@thread
            }

            val random: Random = Random((System.nanoTime() * (Math.random() * 100)).toLong())
            val builder: StringBuilder = StringBuilder()
            for (i: Int in 1..length * 2) {
                builder.append(characters[random.nextInt(characters.size)])
            }

            val finalRandom: Random = Random((System.nanoTime() * (Math.random() * 100)).toLong() * length)
            val finalBuilder: StringBuilder = StringBuilder()
            for (i: Int in 1..length) {
                finalBuilder.append(builder.toString()[finalRandom.nextInt(length * 2)])
            }

            val message: IMessage = event.author.orCreatePMChannel.sendMessage(BotUtils.createSimpleEmbed("Password Generator", "Your random password is: `$finalBuilder` ($length characters long). This message will disappear in 15 seconds", event.author))
            if(!event.channel.isPrivate) {
                BotUtils.sendMessage(BotUtils.createSimpleEmbed("Password Generator", "Alright ${event.author.mention()}, sent you a DM!", event.author), event.channel)
            }

            Thread.sleep(15 * 1000)

            message.edit(BotUtils.createSimpleEmbed("Password Generator", "This password has disappered.", event.author))
        }
    }
}