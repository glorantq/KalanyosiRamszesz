package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import java.util.*

/**
 * Created by glorantq on 2017. 07. 22..
 */
class EmojiConvertCommand : Command {
    override val commandName: String
        get() = "emojiconvert"
    override val description: String
        get() = "Converts your message into emojis"
    override val extendedHelp: String
        get() = "Converts your message into emojis. Works with alphanumeric characters, exclamation marks and question marks"
    override val aliases: List<String>
        get() = listOf("ec", "econv")
    override val permission: Permission
        get() = Permission.USER

    val conversionMap: HashMap<String, String> = HashMap()
    val postProcessMap: HashMap<String, String> = HashMap()
    val letterRegex: Regex = Regex("[a-zA-Z]")

    init {
        conversionMap.put("0", ":zero:")
        conversionMap.put("1", ":one:")
        conversionMap.put("2", ":two:")
        conversionMap.put("3", ":three:")
        conversionMap.put("4", ":four:")
        conversionMap.put("5", ":five:")
        conversionMap.put("6", ":six:")
        conversionMap.put("7", ":seven:")
        conversionMap.put("8", ":eight:")
        conversionMap.put("9", ":nine:")

        conversionMap.put("?", ":grey_question:")
        conversionMap.put("!", ":grey_exclamation:")
        conversionMap.put(" ", "    ")

        postProcessMap.put(":regional_indicator_b:", ":b:")
        postProcessMap.put(":grey_exclamation::grey_exclamation:", ":bangbang:")
        postProcessMap.put(":grey_exclamation::grey_question:", ":interrobang: ")
    }

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val builder: StringBuilder = StringBuilder()
        for(part: String in args) {
            builder.append(part)
            builder.append(" ")
        }
        val message: String = builder.toString().trim()
        val converted: StringBuilder = StringBuilder()

        for(c: Char in message.toCharArray()) {
            if(letterRegex.matches(c.toString())) {
                converted.append(":regional_indicator_${c.toLowerCase()}:")
            } else {
                if(conversionMap.containsKey(c.toString())) {
                    converted.append(conversionMap[c.toString()])
                } else {
                    converted.append(c)
                }
            }
        }

        if(BotUtils.getGuildConfig(event).emojiNameAppend) {
            converted.append(" (${event.message.author.mention()})")
        }

        var convertedMessage: String = converted.toString()

        for((key, value) in postProcessMap) {
            convertedMessage = convertedMessage.replace(key, value)
        }

        if(convertedMessage.length > 2000) {
            event.channel.sendMessage("Sorry ${event.author.mention()}, but the converted message is more than 2000 characters!")
        } else {
            event.channel.sendMessage(convertedMessage)
        }
    }
}