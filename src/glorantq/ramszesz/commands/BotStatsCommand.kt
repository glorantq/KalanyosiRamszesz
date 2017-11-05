package glorantq.ramszesz.commands

import glorantq.ramszesz.utils.BotUtils
import glorantq.ramszesz.utils.tryParseInt
import okhttp3.*
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder
import java.io.IOException

class BotStatsCommand : ICommand {
    override val commandName: String
        get() = "botstats"
    override val description: String
        get() = "Show statistics about this bot"
    override val permission: Permission
        get() = Permission.NONE
    override val availableInDM: Boolean
        get() = true
    override val undocumented: Boolean
        get() = true

    val httpClient: OkHttpClient = OkHttpClient.Builder().build()

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val embed: EmbedBuilder = BotUtils.embed("Bot Statistics", event.author)

        embed.withDescription("Statistics for Kalányosi Ramszesz")
        embed.appendField("Servers", event.client.guilds.size.toString(), true)
        embed.appendField("Users", event.client.guilds.sumBy { it.users.size }.toString(), true)

        val request: Request = Request.Builder().url("https://api.github.com/repos/glorantq/KalanyosiRamszesz/stats/contributors").get().build()
        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(p0: Call?, p1: IOException?) {
                BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Statistics", "Failed to contact GitHub API", event.author), event.channel)
            }

            override fun onResponse(p0: Call?, p1: Response?) {
                try {
                    val json: String = p1!!.body()!!.string() ?: ""
                    if (json.isEmpty()) {
                        BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Statistics", "Failed to contact GitHub API", event.author), event.channel)
                        return
                    }

                    val contributors: ArrayList<Contributor> = ArrayList()

                    val root: JSONArray = JSONParser().parse(json) as JSONArray
                    for (element: Any? in root) {
                        element as JSONObject
                        if (!element.containsKey("weeks")) {
                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Statistics", "Failed to contact GitHub API", event.author), event.channel)
                            return
                        }

                        var additions: Int = 0
                        var deletions: Int = 0

                        for (week: Any? in element["weeks"] as JSONArray) {
                            week as JSONObject
                            if (!week.containsKey("a") || !week.containsKey("d")) {
                                BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Statistics", "Failed to contact GitHub API", event.author), event.channel)
                                return
                            }

                            additions += week["a"].toString().tryParseInt(0)
                            deletions += week["d"].toString().tryParseInt(0)
                        }

                        if (!element.containsKey("author")) {
                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Statistics", "Failed to contact GitHub API", event.author), event.channel)
                            return
                        }

                        val author: JSONObject = element["author"] as JSONObject
                        if (!author.containsKey("login") || !author.containsKey("html_url")) {
                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Statistics", "Failed to contact GitHub API", event.author), event.channel)
                            return
                        }

                        val username: String = author["login"].toString()
                        val link: String = author["html_url"].toString()

                        contributors.add(Contributor(username, link, additions, deletions))
                    }

                    val sortedContributors: List<Contributor> = contributors.sortedWith(Comparator { o1, o2 -> compareValues(o2.additions, o1.additions) })

                    embed.appendField("Contributors (on GitHub)", buildString {
                        for (contributor: Contributor in sortedContributors) {
                            append("[${contributor.username}](${contributor.link}) - ${contributor.additions} additions, ${contributor.deletions} deletions\n")
                        }
                    }, false)
                    BotUtils.sendMessage(embed.build(), event.channel)
                } catch (e: Exception) {
                    val errEmbed: EmbedBuilder = BotUtils.embed("Bot Statistics", event.author)
                    errEmbed.withDescription("Failed to get bot statistics because @glorantq can't code")
                    errEmbed.appendField(e::class.simpleName, e.message, false)
                    BotUtils.sendMessage(errEmbed.build(), event.channel)
                    return
                }
            }
        })
    }

    private class Contributor(val username: String, val link: String, val additions: Int, val deletions: Int)
}