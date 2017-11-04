package glorantq.ramszesz.scripting.api

import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.ThreeArgFunction
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.ZeroArgFunction

class LuaEmbedBuilder : TwoArgFunction() {
    override fun call(modname: LuaValue, env: LuaValue): LuaValue {
        val library: LuaTable = LuaValue.tableOf()
        library.set("new", New())
        env.set("EmbedBuilder", library)
        return library
    }

    private inner class New : ZeroArgFunction() {
        override fun call(): LuaValue = EmbedBuilder()
    }

    private inner class EmbedBuilder : LuaTable() {
        val instance: EmbedBuilder = this
        val embedBuilder: sx.blah.discord.util.EmbedBuilder = sx.blah.discord.util.EmbedBuilder()

        init {
            set("withDescription", WithDescription())
            set("withTitle", WithTitle())
            set("withFooterText", WithFooterText())
            set("withFooterIcon", WithFooterIcon())
            set("withAuthorName", WithAuthorName())
            set("withAuthorUrl", WithAuthorURL())
            set("withAuthorIcon", WithAuthorIcon())
            set("withImage", WithImage())
            set("withThumbnail", WithThumbnail())
            set("withTimestamp", WithTimestamp())
            set("withColor", WithColor())
            set("appendField", AppendField())
            set("build", Build())
        }

        private inner class WithDescription : OneArgFunction() {
            override fun call(p0: LuaValue): LuaValue {
                embedBuilder.withDescription(p0.checkjstring())

                return instance
            }
        }

        private inner class WithTitle : OneArgFunction() {
            override fun call(p0: LuaValue): LuaValue {
                embedBuilder.withTitle(p0.checkjstring())

                return instance
            }
        }

        private inner class WithFooterText : OneArgFunction() {
            override fun call(p0: LuaValue): LuaValue {
                embedBuilder.withFooterText(p0.checkjstring())

                return instance
            }
        }

        private inner class WithFooterIcon : OneArgFunction() {
            override fun call(p0: LuaValue): LuaValue {
                embedBuilder.withFooterIcon(p0.checkjstring())

                return instance
            }
        }

        private inner class WithAuthorName : OneArgFunction() {
            override fun call(p0: LuaValue): LuaValue {
                embedBuilder.withAuthorName(p0.checkjstring())

                return instance
            }
        }

        private inner class WithAuthorURL : OneArgFunction() {
            override fun call(p0: LuaValue): LuaValue {
                embedBuilder.withAuthorUrl(p0.checkjstring())

                return instance
            }
        }

        private inner class WithAuthorIcon : OneArgFunction() {
            override fun call(p0: LuaValue): LuaValue {
                embedBuilder.withAuthorIcon(p0.checkjstring())

                return instance
            }
        }

        private inner class WithTimestamp : OneArgFunction() {
            override fun call(p0: LuaValue): LuaValue {
                embedBuilder.withTimestamp(p0.checklong())

                return instance
            }
        }

        private inner class WithImage : OneArgFunction() {
            override fun call(p0: LuaValue): LuaValue {
                embedBuilder.withImage(p0.checkjstring())

                return instance
            }
        }

        private inner class WithThumbnail : OneArgFunction() {
            override fun call(p0: LuaValue): LuaValue {
                embedBuilder.withThumbnail(p0.checkjstring())

                return instance
            }
        }

        private inner class WithColor : OneArgFunction() {
            override fun call(p0: LuaValue): LuaValue {
                var color = 0

                if(p0.isstring()) {
                    var hex: String = p0.checkjstring()
                    if(hex.startsWith("#")) {
                        hex = hex.replace("#", "")
                        color = try {
                            java.lang.Integer.parseInt(hex, 16)
                        } catch (e: Exception) {
                            0
                        }
                    }
                } else {
                    color = p0.checkint()
                }

                embedBuilder.withColor(color)

                return instance
            }
        }

        private inner class AppendField : ThreeArgFunction() {
            override fun call(p0: LuaValue, p1: LuaValue, p2: LuaValue): LuaValue {
                embedBuilder.appendField(p0.checkjstring(), p1.checkjstring(), p2.checkboolean())

                return instance
            }
        }

        private inner class Build : ZeroArgFunction() {
            override fun call(): LuaValue = LuaEmbed(embedBuilder.build())
        }
    }
}