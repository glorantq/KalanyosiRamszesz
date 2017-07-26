package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import glorantq.ramszesz.config.ConfigFile
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser

/**
 * Created by glora on 2017. 07. 23..
 */
class DeleteCommand : ICommand {
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
    override val usage: String
        get() = "[@Mention] Amount"

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        if(!BotUtils.hasPermissions(8192, event.author, event.guild)) {
            event.channel.sendMessage(BotUtils.createSimpleEmbed("Delete", "You don't have permissions to manage messages!", event.author))
            return
        }

        val config: ConfigFile = BotUtils.getGuildConfig(event)

        if (event.message.mentions.isEmpty()) {
            if (args.isEmpty()) {
                BotUtils.sendUsageEmbed("Specify the number of messages to delete, and optionally a user!", "Delete", event.author, event, this)
                return
            }

            val toDelete: Int
            try {
                toDelete = args[0].toInt() + if(config.deleteCommands) { 0 } else { 1 }
            } catch (e: NumberFormatException) {
                event.channel.sendMessage(BotUtils.createSimpleEmbed("Delete", "`${args[0]}` is not a valid number", event.author))
                return
            }


            if (toDelete > 501) {
                event.channel.sendMessage(BotUtils.createSimpleEmbed("Delete", "Only a maximum of 500 messages can be deleted!", event.author))
                return
            } else if(toDelete < 2) {
                event.channel.sendMessage(BotUtils.createSimpleEmbed("Delete", "You must provide at least one message to delete!", event.author))
                return
            }

            var deletedMessages: Int = 0
            val deleteQueue: ArrayList<IMessage> = ArrayList()
            for (message: IMessage in event.channel.getMessageHistory(toDelete)) {
                deleteQueue.add(message)
                deletedMessages++
                if (deletedMessages >= toDelete) {
                    break
                }
            }

            event.channel.bulkDelete(deleteQueue)

            event.channel.sendMessage(BotUtils.createSimpleEmbed("Delete", "Removed $deletedMessages message(s)!", event.author))
            if(config.logModerations) {
                event.guild.getChannelByID(config.modLogChannel).sendMessage(BotUtils.createSimpleEmbed("Delete", "Removed $deletedMessages message(s) from ${event.channel.mention()}", event.author))
            }
        } else {
            val mentions: List<IUser> = event.message.mentions
            if (args.size == mentions[0].name.split(" ").size) {
                event.channel.sendMessage(BotUtils.createSimpleEmbed("Delete", "Specify the number of messages to delete, and optionally a user!", event.author))
                return
            }
            val toDelete: Int
            try {
                toDelete = args[mentions[0].name.split(" ").size].toInt() + if (mentions[0].longID == event.author.longID) {
                    1
                } else {
                    0
                }
            } catch (e: NumberFormatException) {
                event.channel.sendMessage(BotUtils.createSimpleEmbed("Delete", "`${args[mentions[0].name.split(" ").size]}` is not a valid number", event.author))
                return
            }

            if (toDelete > 501) {
                event.channel.sendMessage(BotUtils.createSimpleEmbed("Delete", "Only a maximum of 500 messages can be deleted!", event.author))
                return
            } else if(toDelete < 1 + if (mentions[0].longID == event.author.longID) { 1 } else { 0 }) {
                event.channel.sendMessage(BotUtils.createSimpleEmbed("Delete", "You must provide at least one message to delete!", event.author))
                return
            }

            var deletedMessages: Int = 0
            val deleteQueue: ArrayList<IMessage> = ArrayList()
            for (message: IMessage in event.channel.getMessageHistory(600)) {
                if (message.author.longID == mentions[0].longID) {
                    deleteQueue.add(message)
                    deletedMessages++
                    if (deletedMessages >= toDelete) {
                        break
                    }
                }
            }

            event.channel.bulkDelete(deleteQueue)
            event.channel.sendMessage(BotUtils.createSimpleEmbed("Delete", "Removed ${deletedMessages - if (mentions[0].longID == event.author.longID) {
                1
            } else {
                0
            }} message(s)!", event.author))

            if (config.logModerations) {
                event.guild.getChannelByID(config.modLogChannel).sendMessage(BotUtils.createSimpleEmbed("Delete", "Removed ${deletedMessages - if (mentions[0].longID == event.author.longID) {
                    1
                } else {
                    0
                }} message(s) from ${event.channel.mention()}", event.author))

            }
        }
    }
}