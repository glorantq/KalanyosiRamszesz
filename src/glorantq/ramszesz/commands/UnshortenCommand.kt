package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.EmbedBuilder
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL
import kotlin.concurrent.thread


/**
 * Created by glora on 2017. 07. 23..
 */
class UnshortenCommand : Command {
    override val commandName: String
        get() = "unshorten"
    override val description: String
        get() = "Unshorten a URL"
    override val permission: Permission
        get() = Permission.USER
    override val extendedHelp: String
        get() = "Unshorten a URL to see where it actually points to"
    override val aliases: List<String>
        get() = listOf("urlreveal", "linkexpand", "expand")
    override val usage: String
        get() = "URL"

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        if(args.isEmpty()) {
            BotUtils.sendUsageEmbed("You need to specify a URL!", "Unshorten", event.author, event, this)
            return
        }

        val message: IMessage = event.channel.sendMessage(BotUtils.createSimpleEmbed("Unshorten", "Checking that link for you...", event.author))

        thread(isDaemon = true) {
            var shortUrl: String = args[0]
            if(!shortUrl.startsWith("http://") && !shortUrl.startsWith("https://")) shortUrl = "http://$shortUrl"

            val url = URL(shortUrl)
            val httpURLConnection = url.openConnection(Proxy.NO_PROXY) as HttpURLConnection

            httpURLConnection.instanceFollowRedirects = false

            val expandedURL = httpURLConnection.getHeaderField("Location")
            httpURLConnection.disconnect()

            val builder: EmbedBuilder = BotUtils.embed("Unshorten", event.author)
            builder.withDescription("Results are in!")
            if(expandedURL == null || expandedURL.isEmpty()) {
                builder.appendField("Failed to contact URL", "$shortUrl is unreachable or is not a short link!", false)
            } else {
                builder.appendField("$shortUrl points to:", expandedURL, false)
            }
            message.edit(builder.build())
        }
    }
}