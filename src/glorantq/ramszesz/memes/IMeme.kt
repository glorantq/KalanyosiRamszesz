package glorantq.ramszesz.memes

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

/**
 * Created by glora on 2017. 07. 26..
 */
interface IMeme {
    val name: String

    var parameters: ArrayList<MemeParameter>

    fun execute(event: MessageReceivedEvent)
}