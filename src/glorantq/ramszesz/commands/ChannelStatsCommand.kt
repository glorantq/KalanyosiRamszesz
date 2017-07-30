package glorantq.ramszesz.commands

import glorantq.ramszesz.utils.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
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

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        thread(name = "ChannelStatsCheck-${event.channel.longID}-${System.nanoTime()}", isDaemon = true, start = true) {
            val messageCounts: HashMap<Long, Int> = HashMap()
            val history: List<IMessage> = event.channel.getMessageHistory(5000)

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
            embed.withDescription("Channel statistics for ${event.channel.mention()} (Total: ${event.channel.fullMessageHistory.size} messages, calculation limited to 5000 messages)")

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