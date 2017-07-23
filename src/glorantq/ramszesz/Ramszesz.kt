package glorantq.ramszesz

import glorantq.ramszesz.commands.*
import glorantq.ramszesz.config.ConfigFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import java.util.*
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
    val discord: IDiscordClient = BotUtils.buildDiscordClient("MzM4Mzc3NjkwMDAyNDg5MzQ0.DFVAug.2_GUFl0XcN627Zz-ImDisABSFpM")

    val configs: ArrayList<ConfigFile> = ArrayList()
    val commands: ArrayList<Command> = ArrayList()

    init {
        logger.info("Starting Kalányosi Ramszesz...")
        commands.add(PingCommand())
        commands.add(HelpCommand())
        commands.add(EmojiConvertCommand())
        commands.add(ConfigDumpCommand())
        commands.add(ConfigCommand())
        commands.add(LeaveCommand())

        discord.dispatcher.registerListener(this)
        discord.login()
    }

    @EventSubscriber
    fun onBotReady(event: ReadyEvent) {
        logger.info("Bot ready with ${configs.size} configs loaded!")

        event.client.changePlayingText("with Viktor's stuff")
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

        if(message.startsWith(BotUtils.prefix, true)) {
            val parts: List<String> = message.split(" ")
            val args: List<String> = if(parts.size == 1) { ArrayList() } else { parts.subList(1, parts.size) }

            val commandBase: String = parts[0].replaceFirst(BotUtils.prefix, "", true)

            for(command: Command in commands) {
                if(command.commandName.equals(commandBase, true) || command.aliases.contains(commandBase.toLowerCase())) {
                    if(getConfigForGuild(event.guild.stringID).deleteCommands) {
                        event.message.delete()
                    }
                    if(BotUtils.getPermissionLevel(event.author, event.guild).ordinal < command.permission.ordinal) {
                        event.channel.sendMessage("I'm sorry ${event.author.mention()}, but you don't have permissions to run this command")
                    } else {
                        command.execute(event, args)
                    }
                    break
                }
            }
        }
    }

    @EventSubscriber
    fun guildCreateEvent(event: GuildCreateEvent) {
        getConfigForGuild(event.guild.stringID)
        logger.info("GuildCreateEvent for ${event.guild.name}")
    }
}