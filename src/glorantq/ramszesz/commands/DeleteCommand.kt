package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage

/**
 * Created by glora on 2017. 07. 23..
 */
class DeleteCommand : Command {
    override val commandName: String
        get() = "delete"
    override val description: String
        get() = "Delete messages"
    override val permission: Permission
        get() = Permission.ADMIN
    override val extendedHelp: String
        get() = "Delete messages. Mention a user to delete messages from that user"
    override val aliases: List<String>
        get() = listOf("purge")

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        if(event.message.mentions.isEmpty()) {
            if(args.isEmpty()) {
                event.channel.sendMessage(BotUtils.createSimpleEmbed("Delete", "Specify the number of messages to delete, and optionally a user!", event.author))
                return
            }

            val toDelete: Int = args[0].toInt() + 1
            if(toDelete > 100) {
                event.channel.sendMessage(BotUtils.createSimpleEmbed("Delete", "Only a maximum of 99 messages can be deleted!", event.author))
                return
            }

            var removedMessages: Int = 0
            for(message: IMessage in event.channel.getMessageHistory(toDelete)) {
                message.delete()
                removedMessages++
            }

            event.channel.sendMessage(BotUtils.createSimpleEmbed("Delete", "Removed $removedMessages messages!", event.author))
        } else {

        }
    }
}