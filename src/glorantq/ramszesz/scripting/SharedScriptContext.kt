package glorantq.ramszesz.scripting

import glorantq.ramszesz.scripting.api.LuaMessageEvent
import glorantq.ramszesz.utils.BotUtils
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaClosure
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Prototype
import org.luaj.vm2.compiler.LuaC
import org.luaj.vm2.lib.jse.JsePlatform
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.EmbedBuilder

class SharedScriptContext {
    var context: Globals = JsePlatform.standardGlobals()
        private set

    private val includes: List<String> = listOf(
            "glorantq.ramszesz.scripting.api.GeneralApi",
            "glorantq.ramszesz.scripting.api.LuaEmbedBuilder"
    )

    init {
        includeLibraries()
    }

    private fun includeLibraries() {
        val includeCode = StringBuilder()
        includes.forEach {
            includeCode.append("require \"")
            includeCode.append(it)
            includeCode.append("\"\n")
        }

        val prototype: Prototype = LuaC.instance.compile(includeCode.toString().byteInputStream(), "includes")
        val closure = LuaClosure(prototype, context)
        closure.call()
    }

    fun runCode(code: String, event: MessageReceivedEvent) {
        val compilingMessage: IMessage = event.channel.sendMessage(BotUtils.createSimpleEmbed("Script Execution", "Compiling code...", event.author))
        context.set("event", LuaMessageEvent(event))
        val closure: LuaClosure
        try {
            val prototype: Prototype = LuaC.instance.compile(code.byteInputStream(), "eval")
            closure = LuaClosure(prototype, context)
        } catch (e: Exception) {
            val embed: EmbedBuilder = BotUtils.embed("Script Execution", event.author)
            embed.withDescription("An error occured while compiling the script!")
            embed.appendField(e::class.simpleName, e.message, false)
            compilingMessage.edit(embed.build())

            context.set("event", LuaValue.NIL)
            return
        }

        try {
            closure.call()
        } catch (e: Exception) {
            e.printStackTrace()

            val embed: EmbedBuilder = BotUtils.embed("Script Execution", event.author)
            embed.withDescription("The currently executed script threw an exception!")
            embed.appendField(e::class.simpleName, e.message, false)
            compilingMessage.edit(embed.build())

            context.set("event", LuaValue.NIL)
            return
        }

        compilingMessage.edit(BotUtils.createSimpleEmbed("Script Execution", "The script was executed without errors.", event.author))
        context.set("event", LuaValue.NIL)
    }

    fun resetContext() {
        context = JsePlatform.standardGlobals()
        includeLibraries()
    }
}