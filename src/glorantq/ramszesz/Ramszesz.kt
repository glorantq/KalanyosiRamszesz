package glorantq.ramszesz

import glorantq.ramszesz.commands.*
import glorantq.ramszesz.config.ConfigFile
import glorantq.ramszesz.utils.BotUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import kotlin.concurrent.thread

/**
 * Created by glorantq on 2017. 07. 22..
 */
class Ramszesz private constructor() {

    private object Singleton {
        val INSTANCE: Ramszesz = Ramszesz()
    }

    companion object {
        val instance: Ramszesz by lazy { Singleton.INSTANCE }
    }

    val logger: Logger = LoggerFactory.getLogger(Ramszesz::class.java)
    val discord: IDiscordClient = BotUtils.buildDiscordClient(discordToken)

    val configs: ArrayList<ConfigFile> = ArrayList()
    val commands: ArrayList<ICommand> = ArrayList()
    val squad: ArrayList<Long> = arrayListOf(
            126481324017057792,
            338439383252336640,
            138988491240505345,
            269452193826865153,
            251374678688530433,
            252070801476419584,
            129213229221019648,
            292976637463756800,
            312356544454852619
    )

    var updatePlayingText: Boolean = true

    init {
        logger.info("Starting Kalányosi Ramszesz...")
        commands.add(PingCommand())
        commands.add(HelpCommand())
        commands.add(EmojiConvertCommand())
        commands.add(ConfigDumpCommand())
        commands.add(ConfigCommand())
        commands.add(LeaveCommand())
        commands.add(AvatarCommand())
        commands.add(JoinCommand())
        commands.add(AssignRoleCommand())
        commands.add(RemoveRoleCommand())
        commands.add(KickCommand())
        commands.add(BanCommand())
        commands.add(PlayingTextCommand())
        commands.add(DeleteCommand())
        commands.add(UnshortenCommand())
        commands.add(ColourCommand())
        commands.add(RoleIDCommand())
        commands.add(ConvertCommand())
        commands.add(MemeCommand())
        commands.add(PasswordCommand())
        commands.add(GuildListCommand())
        commands.add(DictionaryCommand())
        commands.add(ChannelStatsCommand())
        commands.add(UserInfoCommand())
        commands.add(BotStatsCommand())
        commands.add(TestEmbedCommand())
        commands.add(CatCommand())
        commands.add(OsuCommand())

        discord.dispatcher.registerListener(this)
        discord.login()
    }

    @EventSubscriber
    fun onBotReady(event: ReadyEvent) {
        logger.info("Bot ready with ${configs.size} configs loaded!")
        event.client.online()

        thread(isDaemon = true, name = "PlayingStatusUpdater", start = true) {
            var currentText: Int = 0
            while (event.client.isLoggedIn) {
                if (updatePlayingText) {
                    event.client.changePlayingText(when (currentText) {
                        0 -> buildString {
                            append("in ")
                            append(event.client.guilds.size)
                            append(" guilds")
                        }

                        1 -> buildString {
                            var users: Int = 0
                            event.client.guilds.forEach { users += it.users.size }
                            append("with ")
                            append(users)
                            append(" users")
                        }

                        else -> "Try r!help"
                    })

                    logger.info("Updated playing text to id: $currentText")
                    if (++currentText > 2) currentText = 0
                    Thread.sleep(10 * 1000)
                }
            }
        }
    }

    fun getConfigForGuild(guildId: String): ConfigFile {
        configs
                .filter { it.guildId.equals(guildId, true) }
                .forEach { return it }

        logger.info("Tried to get a config for guild $guildId, loading it...")
        val config: ConfigFile = ConfigFile.create(guildId)
        configs.add(config)
        return config
    }

    @EventSubscriber
    fun onMessageReceived(event: MessageReceivedEvent) {
        val message: String = event.message.formattedContent

        if (message.startsWith(BotUtils.prefix, true)) {
            val parts: List<String> = message.split(" ")
            val args: List<String> = if (parts.size == 1) {
                ArrayList()
            } else {
                parts.subList(1, parts.size)
            }

            val commandBase: String = parts[0].replaceFirst(BotUtils.prefix, "", true)

            for (command: ICommand in commands) {
                if (command.commandName.equals(commandBase, true) || command.aliases.contains(commandBase.toLowerCase())) {
                    if(command.disabled) {
                        BotUtils.sendMessage(BotUtils.createSimpleEmbed("Kalányosi Ramszesz", "I'm sorry ${event.author.mention()}, but this command is disabled!", event.author), event.channel)
                        return
                    }
                    if(command.botPermissions != 0x0 && !BotUtils.hasPermissions(command.botPermissions, event.client.ourUser, event.guild)) {
                        BotUtils.sendMessage(BotUtils.createSimpleEmbed("Kalányosi Ramszesz", "I'm sorry ${event.author.mention()}, but I don't have permissions to do this!", event.author), event.channel)
                        return
                    }
                    if (!event.channel.isPrivate && getConfigForGuild(event.guild.stringID).deleteCommands) {
                        try {
                            event.message.delete()
                        } catch(e: Exception) {
                            getConfigForGuild(event.guild.stringID).deleteCommands = false
                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Kalányosi Ramszesz", "I have set the value of `deleteCommands` to `false`, as I don't have permission to delete messages!", event.author), event.channel)
                        }
                    }
                    if (event.channel.isPrivate && !command.availableInDM) {
                        BotUtils.sendMessage(BotUtils.createSimpleEmbed("Kalányosi Ramszesz", "I'm sorry ${event.author.mention()}, but this command don't work in DMs!", event.author), event.channel)
                        return
                    }
                    if (command.permission == Permission.BOT_OWNER && event.author.longID == 251374678688530433) {
                        command.execute(event, args)
                    } else if (command.permission == Permission.SQUAD) {
                        if (squad.contains(event.author.longID)) {
                            command.execute(event, args)
                        } else {
                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Missing Permissions", "I'm sorry ${event.author.mention()}, but only the hyper-extra-super-dev-super-squad members can run this command!", event.author), event.channel)
                        }
                    } else {
                        if (BotUtils.getPermissionLevel(event.author, event.guild).ordinal < command.permission.ordinal) {
                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Missing Permissions", "I'm sorry ${event.author.mention()}, but you don't have permissions to run this command", event.author), event.channel)
                        } else {
                            command.execute(event, args)
                        }
                    }
                    return
                }
            }

            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Invalid Command", "The command `$commandBase` is invalid", event.author), event.channel)
        }
    }

    @EventSubscriber
    fun guildCreateEvent(event: GuildCreateEvent) {
        getConfigForGuild(event.guild.stringID)
        logger.info("GuildCreateEvent for ${event.guild.name}")
    }
}