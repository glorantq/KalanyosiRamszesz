package glorantq.ramszesz.commands

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import glorantq.ramszesz.utils.BotUtils
import glorantq.ramszesz.utils.DictionaryResult
import glorantq.ramszesz.utils.tryParseInt
import okhttp3.*
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IVoiceChannel
import sx.blah.discord.util.EmbedBuilder
import java.io.IOException

/**
 * Created by glora on 2017. 07. 28..
 */
class DictionaryCommand : ICommand {
    override val commandName: String
        get() = "dictionary"
    override val description: String
        get() = "Look up a word in the dictionary"
    override val permission: Permission
        get() = Permission.USER

    val httpClient: OkHttpClient by lazy { OkHttpClient.Builder().build() }

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        if (args.isEmpty()) {
            BotUtils.sendUsageEmbed("You ned to specify a word!", "Dictionary", event.author, event, this)
            return
        }

        var limit: Int = 3
        if(args.size > 1) {
            limit = args[1].tryParseInt(3)
        }

        if(limit == 0) {
            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Dictionary", "0 definitions aren't useful", event.author), event.channel)
            return
        }

        if(limit < 0) {
            limit *= -1
            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Dictionary", "The number `$limit` is negative, it has been changed to `$limit`", event.author), event.channel)
        }

        if(limit > 15) {
            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Dictionary", "The number `$limit` is too large", event.author), event.channel)
            return
        }

        val url: String = "http://api.wordnik.com/v4/word.json/${args[0]}/definitions?limit=$limit&includeRelated=false&sourceDictionaries=ahd%2Ccentury&useCanonical=false&includeTags=false&api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5"

        val request: Request = Request.Builder().url(url).get().build()
        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(p0: Call?, p1: IOException?) {
                BotUtils.sendMessage(BotUtils.createSimpleEmbed("Dictionary", "Failed to look up `${args[0]}` in the dictionary because @glorantq can't code!", event.author), event.channel)
            }

            override fun onResponse(p0: Call?, p1: Response?) {
                if(p1 == null) {
                    BotUtils.sendMessage(BotUtils.createSimpleEmbed("Dictionary", "Failed to look up `${args[0]}` in the dictionary because @glorantq can't code!", event.author), event.channel)
                    return
                }
                val results: List<DictionaryResult> = Gson().fromJson(p1.body()!!.string())

                val embed: EmbedBuilder = BotUtils.embed("Dictionary", event.author)
                embed.withDescription("Definitions for \"${args[0]}\"")

                for(result: DictionaryResult in results) {
                    embed.appendField("From ${result.sourceDictionary}", "${result.text}\n\n`Data sourced ${result.attributionText}`", false)
                }

                BotUtils.sendMessage(embed.build(), event.channel)
            }
        })
    }
}