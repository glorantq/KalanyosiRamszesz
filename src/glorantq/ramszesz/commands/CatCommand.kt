package glorantq.ramszesz.commands

import glorantq.ramszesz.utils.BotUtils
import okhttp3.*
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder
import java.io.IOException

class CatCommand : ICommand {
    override val commandName: String
        get() = "cat"
    override val description: String
        get() = "Get a random cat"
    override val permission: Permission
        get() = Permission.USER

    val httpClient: OkHttpClient = OkHttpClient.Builder().build()

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val request: Request = Request.Builder().url("http://random.cat/meow").get().build()
        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(p0: Call?, p1: IOException?) {
                BotUtils.sendMessage(BotUtils.createSimpleEmbed("Random Cat", "Failed to reach the kittens.", event.author), event.channel)
            }

            override fun onResponse(p0: Call?, p1: Response?) {
                val json: String = p1!!.body()!!.string() ?: ""
                if (json.isEmpty()) {
                    BotUtils.sendMessage(BotUtils.createSimpleEmbed("Random Cat", "Failed to reach the kittens.", event.author), event.channel)
                    return
                }

                val root: JSONObject = JSONParser().parse(json) as JSONObject
                if(!root.containsKey("file")) {
                    BotUtils.sendMessage(BotUtils.createSimpleEmbed("Random Cat", "Failed to reach the kittens.", event.author), event.channel)
                    return
                }

                val url: String = root["file"].toString()
                val embed: EmbedBuilder = BotUtils.embed("Random Cat", event.author)
                embed.withImage(url)

                BotUtils.sendMessage(embed.build(), event.channel)
            }
        })
    }
}