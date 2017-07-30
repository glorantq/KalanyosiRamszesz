package glorantq.ramszesz.commands

import glorantq.ramszesz.utils.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

/**
 * Created by glora on 2017. 07. 25..
 */
class ConvertCommand : ICommand {
    override val commandName: String
        get() = "convert"
    override val description: String
        get() = "Convert between decimal and hexadecimal"
    override val permission: Permission
        get() = Permission.USER
    override val usage: String
        get() = "From"
    override val availabeInDM: Boolean
        get() = true

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        if(args.isEmpty()) {
            BotUtils.sendUsageEmbed("You need to provide a number!", "Convert", event.author, event, this)
            return
        }

        var number: String = args[0].toUpperCase()
        if(number.startsWith("0x", true)) {
            number = number.removePrefix("0X").replace(Regex("[^0-9A-F]"), "")
            try {
                val output: Int = number.toInt(16)
                BotUtils.sendMessage(BotUtils.createSimpleEmbed("Convert", "`0x${if (number.length > 1) {
                    number.replaceFirst("0", "")
                } else {
                    number
                }}` in decimal is `$output`", event.author), event.channel)
            } catch (e: NumberFormatException) {
                BotUtils.sendMessage(BotUtils.createSimpleEmbed("Convert", "`0x${if (number.length > 1) {
                    number.replaceFirst("0", "")
                } else {
                    number
                }}` is too large", event.author), event.channel)
                return
            }
        } else {
            number = number.replace(Regex("[^0-9]"), "")
            if(number.isEmpty()) {
                BotUtils.sendMessage(BotUtils.createSimpleEmbed("Convert", "`${args[0].toUpperCase()}` is not a valid number", event.author), event.channel)
                return
            }
            if(number.toDouble() > Integer.MAX_VALUE) {
                BotUtils.sendMessage(BotUtils.createSimpleEmbed("Convert", "The number `$number` is too large", event.author), event.channel)
                return
            }
            val output: String = number.toInt().toString(16).toUpperCase()
            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Convert", "`$number` in hexadecimal is `0x$output`", event.author), event.channel)
        }
    }
}