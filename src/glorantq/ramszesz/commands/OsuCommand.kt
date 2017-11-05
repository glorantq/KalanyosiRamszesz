package glorantq.ramszesz.commands

import com.cedarsoftware.util.io.JsonWriter
import com.overzealous.remark.Remark
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
import java.util.regex.Pattern

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

    /*
    <:A_small:352910388892925953>
    <:B_small:352910392210620417>
    <:C_small:352910397734387713>
    <:D_small:352910397529128961>
    <:S_small:352910397667409931>
    <:SH_small:352910397755359233>
    <:XH_small:352910397897965568>
     */

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
                    val rawJson: String = p1.body()!!.string()

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
                    val embed: EmbedBuilder = BotUtils.embed("osu! statistics for ${user["username"]}", event.author)
                    embed.withAuthorUrl("https://osu.ppy.sh/u/$encodedName")
                    embed.withAuthorIcon("https://osu.ppy.sh/images/layout/osu-logo.png")
                    embed.withThumbnail("https://a.ppy.sh/${user["user_id"]}")

                    embed.appendField("PP", formatNumber(user["pp_raw"]), true)
                    embed.appendField("Global Rank", "#${user["pp_rank"]}", true)
                    embed.appendField("Country", ":flag_${user["country"].toString().toLowerCase()}: ${Locale("", user["country"].toString()).displayCountry}", true)
                    embed.appendField("Country Rank", "#${user["pp_country_rank"]}", true)
                    embed.appendField("Ranked Score", formatNumber(user["ranked_score"]), true)
                    embed.appendField("Total Score", formatNumber(user["total_score"]), true)
                    embed.appendField("Level", formatNumber(user["level"]), true)
                    embed.appendField("Accuracy", "${String.format("%.2f", user["accuracy"].toString().toDouble())}%", true)
                    embed.appendField("Play Count", formatNumber(user["playcount"]), true)
                    embed.appendField("Ranks (SS/S/A)", "${user["count_rank_ss"]}/${user["count_rank_s"]}/${user["count_rank_a"]}", true)

                    val events: JSONArray = user["events"] as JSONArray
                    if(events.isNotEmpty()) {
                        val eventBuilder = StringBuilder()
                        for(osuEvent: Any? in events) {
                            osuEvent as JSONObject
                            var title: String = osuEvent["display_html"].toString()
                            title = title.replace("<img src='/images/A_small.png'/>", "<:A_small:352910388892925953>")
                            title = title.replace("<img src='/images/B_small.png'/>", "<:B_small:352910392210620417>")
                            title = title.replace("<img src='/images/C_small.png'/>", "<:C_small:352910397734387713>")
                            title = title.replace("<img src='/images/D_small.png'/>", "<:D_small:352910397529128961>")
                            title = title.replace("<img src='/images/S_small.png'/>", "<:S_small:352910397667409931>")
                            title = title.replace("<img src='/images/SH_small.png'/>", "<:SH_small:352910397755359233>")
                            title = title.replace("<img src='/images/XH_small.png'/>", "<:XH_small:352910397897965568>")

                            title = Remark().convert(title)
                            title = title.replace("\\_", "_")

                            if(eventBuilder.length + title.length > 1024) {
                                break
                            }

                            eventBuilder.append(title)
                            eventBuilder.append("\n")
                        }
                        eventBuilder.setLength(eventBuilder.length - 1)
                        embed.appendField("Recent Events", eventBuilder.toString(), false)
                    }

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