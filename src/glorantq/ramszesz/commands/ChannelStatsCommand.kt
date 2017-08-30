package glorantq.ramszesz.commands

import glorantq.ramszesz.utils.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by glorantq on 2017. 07. 30..
 */
class ChannelStatsCommand : ICommand {
    override val commandName: String
        get() = "channelstats"
    override val description: String
        get() = "Check statistics for a channel"
    override val permission: Permission
        get() = Permission.ADMIN
    override val usage: String
        get() = "[#Mention]"

    private val timeouts: HashMap<Long, Long> = HashMap()

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val nextExecute: Long  = timeouts[event.guild.longID] ?: 0

        if(nextExecute > System.currentTimeMillis()) {
            val embed: EmbedBuilder = BotUtils.embed("Channel Statistics", event.author)
            embed.withDescription("Due to this command requiring more computational power, it has been given a limit of `1 execution per 15 minutes per guild`")


            val dateTime: ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(nextExecute), ZoneId.systemDefault())
            val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
            val diff: Date = Date(nextExecute - System.currentTimeMillis())
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = diff

            embed.appendField("Next execution available at", "${formatter.format(dateTime)} (${calendar.get(Calendar.MINUTE)} minutes ${calendar.get(Calendar.SECOND)} seconds from now)", false)

            BotUtils.sendMessage(embed.build(), event.channel)
            return
        } else {
            timeouts.put(event.guild.longID, System.currentTimeMillis() + 900000)
        }

        thread(name = "ChannelStatsCheck-${event.channel.longID}-${System.nanoTime()}", isDaemon = true, start = true) {
            val channel: IChannel = if(event.message.channelMentions.isEmpty()) {
                event.channel
            } else {
                event.message.channelMentions[0]
            }

            var hasPerms: Boolean = false
            channel.getModifiedPermissions(event.client.ourUser).forEach { if(it.hasPermission(1024)) { hasPerms = true }}
            if(!hasPerms) {
                BotUtils.sendMessage(BotUtils.createSimpleEmbed("Kalányosi Ramszesz", "I'm sorry ${event.author.mention()}, but I don't have permissions to do this!", event.author), event.channel)
                return@thread
            }

            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Channel Statistics", "Calculating statistics for ${channel.mention()}, this may take a while...", event.author), event.channel)

            val messageCounts: HashMap<Long, Int> = HashMap()
            val history: List<IMessage> = channel.getMessageHistory(5000)

            history.map { it.author }
                    .forEach {
                        if (messageCounts.containsKey(it.longID)) {
                            messageCounts[it.longID] = messageCounts[it.longID]!!.plus(1)
                        } else {
                            messageCounts.put(it.longID, 1)
                        }
                    }

            val finalMap = messageCounts.toSortedMap(Comparator<Long> { o1, o2 -> compareValues(messageCounts[o2], messageCounts[o1]) })

            val embed: EmbedBuilder = BotUtils.embed("Channel Statistics", event.author)
            embed.withDescription(
                    "Channel statistics for ${channel.mention()}\n" +
                    "**${channel.fullMessageHistory.size}** messages, calculation limited to 5000 messages\n" +
                    "**${messageCounts.size}** users have talked recently")

            var added: Int = 0
            for((key, value) in finalMap) {
                val user: IUser? = event.guild.getUserByID(key)
                val username: String = if(user == null) { "Invalid User" } else { user.name }
                val percentage: Double = value.toDouble() / history.size.toDouble() * 100
                embed.appendField(username, "${String.format("%.0f", percentage)}% - $value messages", false)
                if(++added == 5) {
                    break
                }
            }

            BotUtils.sendMessage(embed.build(), event.channel)
        }
    }
}