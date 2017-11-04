package glorantq.ramszesz.commands

import glorantq.ramszesz.utils.BotUtils
import glorantq.ramszesz.config.ConfigFile
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IRole
import java.util.*

/**
 * Created by glorantq on 2017. 07. 23..
 */
class ConfigCommand : ICommand {
    override val commandName: String
        get() = "config"
    override val description: String
        get() = "Change config values"
    override val permission: Permission
        get() = Permission.ADMIN
    override val usage: String
        get() = "Key Value"

    val allowedKeys: ArrayList<String> = ArrayList()

    init {
        allowedKeys.add("emojiNameAppend")
        allowedKeys.add("deleteCommands")
        allowedKeys.add("userRole")
        allowedKeys.add("adminRole")
        allowedKeys.add("logModerations")
        allowedKeys.add("modLogChannel")
        allowedKeys.add("statsCalcLimit")
    }

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        if(args.size < 2) {
            BotUtils.sendUsageEmbed("You need to specify both the config key and value!", "Bot Config", event.author, event, this)
        } else {
            val config: ConfigFile = BotUtils.getGuildConfig(event)
            if(!allowedKeys.contains(args[0])) {
                BotUtils.sendMessage("${event.author.mention()}, the key `${args[0]}` isn't valid!", event.channel)
            } else {
                when(args[0]) {
                    "emojiNameAppend" -> {
                        config.emojiNameAppend = args[1].equals("true", true)
                        BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Config", "The value of `emojiNameAppend` has been set to ${config.emojiNameAppend}", event.author), event.channel)
                    }

                    "deleteCommands" -> {
                        config.deleteCommands = args[1].equals("true", true)
                        BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Config", "The value of `deleteCommands` has been set to ${config.deleteCommands}", event.author), event.channel)
                    }

                    "adminRole" -> {
                        var roleName: String = ""
                        args
                                .filterNot { it.contains("Role") }
                                .forEach { roleName += "$it " }
                        roleName = roleName.trim()

                        if(roleName.equals("owner", true)) {
                            config.adminRole = -1L
                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Config", "The value of `adminRole` has been set to Server Owner", event.author), event.channel)
                            return
                        }

                        val roles: List<IRole> = event.guild.getRolesByName(roleName)
                        if(roles.isEmpty()) {
                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Config", "That role doesn't exist!", event.author), event.channel)
                        } else {
                            config.adminRole = roles[0].longID
                        }
                        BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Config", "The value of `adminRole` has been set to ${roles[0].name}", event.author), event.channel)
                    }

                    "userRole" -> {
                        var roleName: String = ""
                        args
                                .filterNot { it.contains("Role") }
                                .forEach { roleName += "$it " }
                        roleName = roleName.trim()

                        if(roleName.equals("everyone", true)) {
                            config.userRole = -1L
                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Config", "The value of `userRole` has been set to Everyone", event.author), event.channel)
                            return
                        }

                        val roles: List<IRole> = event.guild.getRolesByName(roleName)
                        if(roles.isEmpty()) {
                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Config", "That role doesn't exist!", event.author), event.channel)
                        } else {
                            config.userRole = roles[0].longID
                        }
                        BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Config", "The value of `userRole` has been set to ${roles[0].name}", event.author), event.channel)
                    }

                    "logModerations" -> {
                        if(config.modLogChannel == -1L) {
                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Config", "You need to set `modLogChannel` before setting `logModerations`", event.author), event.channel)
                            return
                        }

                        config.logModerations = args[1].equals("true", true)
                        BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Config", "The value of `logModerations` has been set to ${config.logModerations}", event.author), event.channel)
                    }

                    "modLogChannel" -> {
                        if(args[1].equals("none", true)) {
                            config.logModerations = false
                            config.modLogChannel = -1L

                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Config", "The value of `modLogChannel` has been set to none and logging was disabled", event.author), event.channel)
                            return
                        }

                        val channels: List<IChannel> = event.guild.getChannelsByName(args[1])
                        if(channels.isEmpty()) {
                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Config", "Couldn't find that channel!", event.author), event.channel)
                        } else {
                            config.modLogChannel = channels[0].longID
                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Config", "The value of `modLogChannel` has been set to ${channels[0].name}", event.author), event.channel)
                            channels[0].sendMessage(BotUtils.createSimpleEmbed("Moderation Logging", "Moderation logging has been bound to this channel", event.author))
                        }
                    }

                    "statsCalcLimit" -> {
                        if(args.isNotEmpty()) {
                            if(!BotUtils.getSpecialPermissions(event.author).contains(Permission.BOT_OWNER)) {
                                BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Config", "This limit can only be changed by the bot owner!", event.author), event.channel)
                                return
                            }
                            val calcLimit: Int = args[1].toIntOrNull() ?: 5000
                            config.statsCalcLimit = calcLimit
                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Bot Config", "The value of `statsCalcLimit` has been set to $calcLimit", event.author), event.channel)
                        }
                    }
                }
            }
        }
    }
}