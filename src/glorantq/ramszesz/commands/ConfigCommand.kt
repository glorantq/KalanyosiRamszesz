package glorantq.ramszesz.commands

import glorantq.ramszesz.BotUtils
import glorantq.ramszesz.config.ConfigFile
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IRole

/**
 * Created by glorantq on 2017. 07. 23..
 */
class ConfigCommand : Command {
    override val commandName: String
        get() = "config"
    override val description: String
        get() = "Change config values"
    override val permission: Permission
        get() = Permission.ADMIN

    val allowedKeys: ArrayList<String> = ArrayList()

    init {
        allowedKeys.add("emojiNameAppend")
        allowedKeys.add("deleteCommands")
        allowedKeys.add("userRole")
        allowedKeys.add("adminRole")
    }

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        if(args.size < 2) {
            event.channel.sendMessage("${event.author.mention()}, specify both the config key and value!")
        } else {
            val config: ConfigFile = BotUtils.getGuildConfig(event)
            if(!allowedKeys.contains(args[0])) {
                event.channel.sendMessage("${event.author.mention()}, the key `${args[0]}` isn't valid!")
            } else {
                when(args[0]) {
                    "emojiNameAppend" -> {
                        config.emojiNameAppend = args[1].equals("true", true)
                        event.channel.sendMessage("The value of `emojiNameAppend` has been set to ${config.emojiNameAppend}")
                    }

                    "deleteCommands" -> {
                        config.deleteCommands = args[1].equals("true", true)
                        event.channel.sendMessage("The value of `deleteCommands` has been set to ${config.deleteCommands}")
                    }

                    "adminRole" -> {
                        var roleName: String = ""
                        args
                                .filterNot { it.contains("Role") }
                                .forEach { roleName += "$it " }
                        roleName = roleName.trim()

                        if(roleName.equals("-1", true)) {
                            config.adminRole = -1L
                            event.channel.sendMessage("The value of `adminRole` has been set to Server Owner")
                            return
                        }

                        val roles: List<IRole> = event.guild.getRolesByName(roleName)
                        if(roles.isEmpty()) {
                            event.channel.sendMessage("That role doesn't exist!")
                        } else {
                            config.adminRole = roles[0].longID
                        }
                        event.channel.sendMessage("The value of `adminRole` has been set to ${roles[0].name}")
                    }

                    "userRole" -> {
                        var roleName: String = ""
                        args
                                .filterNot { it.contains("Role") }
                                .forEach { roleName += "$it " }
                        roleName = roleName.trim()

                        if(roleName.equals("-1", true)) {
                            config.userRole = -1L
                            event.channel.sendMessage("The value of `userRole` has been set to Everyone")
                            return
                        }

                        val roles: List<IRole> = event.guild.getRolesByName(roleName)
                        if(roles.isEmpty()) {
                            event.channel.sendMessage("That role doesn't exist!")
                        } else {
                            config.userRole = roles[0].longID
                        }
                        event.channel.sendMessage("The value of `userRole` has been set to ${roles[0].name}")
                    }
                }
            }
        }
    }
}