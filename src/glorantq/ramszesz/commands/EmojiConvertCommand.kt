package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import java.util.*

/**
 * Created by glorantq on 2017. 07. 22..
 */
class EmojiConvertCommand : ICommand {
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
    override val usage: String
        get() = "Message"
    override val availabeInDM: Boolean
        get() = true

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
        conversionMap.put("#", ":hash:")
        conversionMap.put("*", ":asterisk:")
        conversionMap.put("@", "<:ramszesz_emoji_at:340218056192163842>")
        conversionMap.put("&", "<:ramszesz_emoji_and:340221936426680320>")
        conversionMap.put(")", "<:ramszesz_emoji_close_bracket:340222063203844108>")
        conversionMap.put("]", "<:ramszesz_emoji_close_sqbracket:340222079473287178>")
        conversionMap.put(",", "<:ramszesz_emoji_comma:340224584013840384>")
        conversionMap.put("-", "<:ramszesz_emoji_dash:340222099975045123>")
        conversionMap.put("$", "<:ramszesz_emoji_dollar:340222111320768512>")
        conversionMap.put(".", "<:ramszesz_emoji_dot:340222120476803094>")
        conversionMap.put("=", "<:ramszesz_emoji_equals:340222131134791680>")
        conversionMap.put("€", "<:ramszesz_emoji_euro:340222142983438358>")
        conversionMap.put("(", "<:ramszesz_emoji_open_bracket:340222174138728448>")
        conversionMap.put("[", "<:ramszesz_emoji_open_sqbracket:340222079473287178>")
        conversionMap.put("\"", "<:ramszesz_emoji_quotes:340222194636292096>")
        conversionMap.put("ß", "<:ramszesz_emoji_ss:340222228157300737>")
        conversionMap.put(";", "<:ramszesz_emoji_semicolon:340222208427294730>")
        conversionMap.put("<", "<:ramszesz_emoji_lt:340222163455836180>")
        conversionMap.put(">", "<:ramszesz_emoji_gt:340222154454859777>")
        conversionMap.put(":", "<:ramszesz_emoji_colon:340225548188123136>")
        conversionMap.put("/", "<:ramszesz_emoji_slash:340225606216318986>")
        conversionMap.put("+", "<:ramszesz_emoji_plus:340225604265705472>")
        conversionMap.put("%", "<:ramszesz_emoji_percent:340225585525555200>")
        conversionMap.put(" ", "<:ramszesz_emoji_space:340231688997306378>")
        conversionMap.put("_", "<:ramszesz_emoji_underscore:340234186113679360>")
        conversionMap.put("\\", "<:ramszesz_emoji_backslash:340235599749447699>")

        postProcessMap.put(":regional_indicator_b:", ":b:")
        postProcessMap.put(":grey_exclamation::grey_exclamation:", ":bangbang:")
        postProcessMap.put(":grey_exclamation::grey_question:", ":interrobang: ")
    }

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        if(args.isEmpty()) {
            BotUtils.sendUsageEmbed("You need to specify a message!", "Emoji Convert", event.author, event, this)
            return
        }

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
            event.channel.sendMessage(BotUtils.createSimpleEmbed("Emoji Convert", "Sorry ${event.author.name}, but the converted message is more than 2000 characters!", event.author))
        } else {
            event.channel.sendMessage(convertedMessage)
        }
    }
}