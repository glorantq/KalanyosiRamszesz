package glorantq.ramszesz.commands

import glorantq.ramszesz.Ramszesz
import glorantq.ramszesz.utils.BotUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage
import java.net.URL

class ExecCommand : ICommand {
    override val commandName: String
        get() = "exec"
    override val description: String
        get() = "Execute scripts"
    override val permission: Permission
        get() = Permission.BOT_OWNER
    override val undocumented: Boolean
        get() = true
    override val usage: String
        get() = "[script] (can be an attachment)"
    override val availableInDM: Boolean
        get() = true

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val rawCode: String
        when {
            event.message.attachments.isNotEmpty() -> {
                val attachment: IMessage.Attachment = event.message.attachments[0]
                try {
                    val url = URL(attachment.url)
                    val request: Request = Request.Builder().url(url).get().build()
                    val response: Response = OkHttpClient.Builder().build().newCall(request).execute()
                    rawCode = response.body()!!.string()
                } catch (e: Exception) {
                    BotUtils.sendMessage(BotUtils.createSimpleEmbed("Script Execution", "Failed to process attachment!", event.author), event.channel)
                    return
                }
            }
            args.isNotEmpty() -> rawCode = args.joinToString(" ")
            else -> {
                BotUtils.sendUsageEmbed("No code supplied!", "Script Execution", event.author, event, this)
                return
            }
        }

        Ramszesz.instance.scriptingContext.runCode(rawCode, event)
    }
}