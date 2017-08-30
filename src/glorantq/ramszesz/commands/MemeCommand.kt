package glorantq.ramszesz.commands

import glorantq.ramszesz.utils.BotUtils
import glorantq.ramszesz.memes.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by glora on 2017. 07. 26..
 */
class MemeCommand : ICommand {
    override val commandName: String
        get() = "memegen"
    override val description: String
        get() = "Generate memes in Discord"
    override val permission: Permission
        get() = Permission.SQUAD
    override val extendedHelp: String
        get() = "Generate memes in Discord."
    override val aliases: List<String>
        get() = listOf("meme")
    override val usage: String
        get() = "Meme [Arguments]"

    companion object {
        val memes: ArrayList<IMeme> = ArrayList()
    }

    init {
        memes.add(MemeList())
        memes.add(TriggeredMeme())
        memes.add(NumberOneMeme())
        memes.add(ThinkingMeme())
        memes.add(EmojiMovieMeme())
        memes.add(RainbowMeme())
    }

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    val checkIDRegex: Regex = Regex("^<@![0-9]{18}>$|^<@[0-9]{18}>$")
    val replaceTagsRegex: Regex = Regex("<@!|<@|>")

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        if(args.isEmpty()) {
            BotUtils.sendUsageEmbed("You need to provide a meme, and optionally arguments", "Meme", event.author, event, this)
            return
        }

        val parts: List<String> = event.message.content.split(" ")
        val memeArgs: List<String> = if (parts.size == 1) {
            java.util.ArrayList()
        } else {
            parts.subList(1, parts.size)
        }

        for(meme: IMeme in memes) {
            if(meme.name.equals(args[0], true)) {
                if(meme.parameters.isNotEmpty()) {
                    if(memeArgs.size - 1 < meme.parameters.size) {
                        BotUtils.sendUsageEmbed("This meme requires more arguments!", "Meme", event.author, event, this)
                        return
                    } else if(memeArgs.size - 1 > meme.parameters.size) {
                        BotUtils.sendUsageEmbed("This meme requires fewer arguments!", "Meme", event.author, event, this)
                        return
                    } else {
                        for(i: Int in 1..memeArgs.size - 1) {
                            when(meme.parameters[i - 1].type) {
                                MemeParameter.Companion.Type.STRING -> {
                                    setParam(i - 1, meme, memeArgs[i])
                                }

                                MemeParameter.Companion.Type.INT -> {
                                    try {
                                        setParam(i - 1, meme, memeArgs[i].toInt())
                                    } catch (e: NumberFormatException) {
                                        BotUtils.sendMessage(BotUtils.createSimpleEmbed("Meme", "`${memeArgs[i]}` is not a valid number!", event.author), event.channel)
                                        return
                                    }
                                }

                                MemeParameter.Companion.Type.USER -> {
                                    val idParam: String = memeArgs[i]
                                    val userId: Long
                                    if(idParam.equals("random_user", true)) {
                                        userId = event.guild.users[Random().nextInt(event.guild.users.size)].longID
                                    } else {
                                        if (!idParam.matches(checkIDRegex)) {
                                            BotUtils.sendMessage(BotUtils.createSimpleEmbed("Meme", "`$idParam` is not a valid tag!", event.author), event.channel)
                                            return
                                        } else {
                                            userId = idParam.replace(replaceTagsRegex, "").toLong()
                                        }
                                    }

                                    val user: IUser? = event.guild.getUserByID(userId)

                                    if(user == null) {
                                        BotUtils.sendMessage(BotUtils.createSimpleEmbed("Meme", "`$userId` is not a valid ID!", event.author), event.channel)
                                        return
                                    } else {
                                        setParam(i - 1, meme, user)
                                    }
                                }
                            }
                        }
                    }
                }

                exec(event, meme)
                return
            }
        }

        BotUtils.sendMessage(BotUtils.createSimpleEmbed("Meme Generator", "`${args[0]}` is not a valid meme", event.author), event.channel)
    }

    private fun exec(event: MessageReceivedEvent, meme: IMeme) {
        thread(name = "MemeExec-${meme.name}-${event.author.name}-${System.nanoTime()}", isDaemon = true, start = true) {
            logger.info("Executing meme...")
            try {
                meme.execute(event)
            } catch (e: Exception) {
                val embed: EmbedBuilder = BotUtils.embed("Meme Generator", event.author)
                embed.withDescription("I couldn't deliver your meme because @glorantq can't code")
                embed.appendField(e::class.simpleName, e.message, false)
                BotUtils.sendMessage(embed.build(), event.channel)
            }
            logger.info("Finished!")
        }
    }

    private fun setParam(index: Int, meme: IMeme, value: Any) {
        val param: MemeParameter = meme.parameters[index]
        param.value = value
        val parameters: ArrayList<MemeParameter> = meme.parameters.clone() as ArrayList<MemeParameter>
        parameters[index] = param
        meme.parameters = parameters
        println(meme.parameters[index].value)
    }
}