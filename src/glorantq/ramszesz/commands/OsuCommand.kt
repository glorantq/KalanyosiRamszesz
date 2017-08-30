package glorantq.ramszesz.commands

import glorantq.ramszesz.osuKey
import glorantq.ramszesz.utils.BotUtils
import okhttp3.*
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder
import java.io.IOException
import java.net.URLEncoder
import java.text.DecimalFormat
import java.util.*

class OsuCommand : ICommand {
    override val commandName: String
        get() = "osu"
    override val description: String
        get() = "Check an osu! player's stats"
    override val permission: Permission
        get() = Permission.USER
    override val usage: String
        get() = "username"
    override val availableInDM: Boolean
        get() = true

    private val httpClient: OkHttpClient by lazy { OkHttpClient.Builder().build() }
    private val numberFormat: DecimalFormat = DecimalFormat("###,###")

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        if (args.isEmpty()) {
            BotUtils.sendUsageEmbed("Specify a user!", "osu! statistics", event.author, event, this)
        } else {
            val username: String = buildString {
                for(s: String in args) {
                    append(s)
                    append(" ")
                }

                setLength(length - 1)
            }

            val encodedName: String = URLEncoder.encode(username, "UTF-8").replace("+", "%20")
            val requestURL: String = "https://osu.ppy.sh/api/get_user?k=$osuKey&u=$encodedName"
            val request: Request = Request.Builder().url(requestURL).get().build()

            httpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(p0: Call, p1: IOException) {
                    BotUtils.sendMessage(BotUtils.createSimpleEmbed("osu! statistics", "Failed to look up `$username`'s stats because @glorantq can't code!", event.author), event.channel)
                }

                override fun onResponse(p0: Call, p1: Response) {
                    val rawJson: String = p1.body().string()

                    val root: Any = JSONParser().parse(rawJson)
                    if (root is JSONObject) {
                        BotUtils.sendMessage(BotUtils.createSimpleEmbed("osu! statistics", buildString {
                            append("Failed to look up `$username`'s stats because @glorantq can't code!")
                            if (root.containsKey("error")) {
                                append(" (${root["error"].toString()})")
                            }
                        }, event.author), event.channel)

                        return
                    }

                    root as JSONArray
                    if (root.isEmpty()) {
                        BotUtils.sendMessage(BotUtils.createSimpleEmbed("osu! statistics", "The user `$username` is invalid!", event.author), event.channel)
                        return
                    }

                    val user: JSONObject = root[0] as JSONObject
                    val embed: EmbedBuilder = BotUtils.embed("osu! statistics for $username", event.author)
                    embed.withAuthorUrl("https://osu.ppy.sh/u/$encodedName")
                    embed.withAuthorIcon("https://osu.ppy.sh/images/layout/osu-logo.png")
                    embed.withThumbnail("https://a.ppy.sh/${user["user_id"]}")

                    embed.appendField("PP", formatNumber(user["pp_raw"]), true)
                    embed.appendField("Global Rank", "#${user["pp_rank"]}", true)
                    embed.appendField("Country", Locale("", user["country"].toString()).displayCountry, true)
                    embed.appendField("Country Rank", "#${user["pp_country_rank"]}", true)
                    embed.appendField("Ranked Score", formatNumber(user["ranked_score"]), true)
                    embed.appendField("Total Score", formatNumber(user["total_score"]), true)
                    embed.appendField("Level", formatNumber(user["level"]), true)
                    embed.appendField("Accuracy", "${String.format("%.2f", user["accuracy"].toString().toDouble())}%", true)
                    embed.appendField("Play Count", formatNumber(user["playcount"]), true)
                    embed.appendField("Ranks", "${user["count_rank_ss"]}/${user["count_rank_s"]}/${user["count_rank_a"]}", true)

                    BotUtils.sendMessage(embed.build(), event.channel)
                }
            })
        }
    }

    private fun formatNumber(input: Any?): String {
        val number: Double =
                Math.round(
                        try {
                            input.toString().toDouble()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            -1.0
                        }).toDouble()
        return numberFormat.format(number)
    }
}