package glorantq.ramszesz.scripting.api

import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import sx.blah.discord.api.internal.json.objects.EmbedObject

class LuaEmbed(val embed: EmbedObject) : LuaTable() {
    init {
        set("title", LuaValue.valueOf(embed.title ?: ""))
        set("description", LuaValue.valueOf(embed.description ?: ""))
        set("color", LuaValue.valueOf(embed.color))
        set("url", LuaValue.valueOf(embed.url ?: ""))
        set("type", LuaValue.valueOf(embed.type ?: ""))
        set("timestamp", LuaValue.valueOf(embed.timestamp ?: ""))

        set("image", if(embed.image == null) { LuaValue.NIL } else { LuaImageObject(embed.image) })
        set("author", if(embed.author == null) { LuaValue.NIL } else { LuaAuthorObject(embed.author) })
        set("footer", if(embed.footer == null) { LuaValue.NIL } else { LuaFooterObject(embed.footer) })
        set("fields", LuaValue.listOf(embed.fields.map { LuaEmbedField(it) }.toTypedArray()))
    }

    inner class LuaImageObject(val image: EmbedObject.ImageObject) : LuaTable() {
        init {
            set("url", LuaValue.valueOf(image.url))
            set("proxyUrl", LuaValue.valueOf(image.proxy_url))
            set("width", LuaValue.valueOf(image.width))
            set("height", LuaValue.valueOf(image.height))
        }
    }

    inner class LuaAuthorObject(val authorObject: EmbedObject.AuthorObject) : LuaTable() {
        init {
            set("iconUrl", LuaValue.valueOf(authorObject.icon_url ?: ""))
            set("iconProxyUrl", LuaValue.valueOf(authorObject.proxy_icon_url ?: ""))
            set("name", LuaValue.valueOf(authorObject.name ?: ""))
            set("url", LuaValue.valueOf(authorObject.url ?: ""))
        }
    }

    inner class LuaFooterObject(val footer: EmbedObject.FooterObject) : LuaTable() {
        init {
            set("iconUrl", LuaValue.valueOf(footer.icon_url ?: ""))
            set("iconProxyUrl", LuaValue.valueOf(footer.proxy_icon_url ?: ""))
            set("text", LuaValue.valueOf(footer.text ?: ""))
        }
    }

    inner class LuaEmbedField(val field: EmbedObject.EmbedFieldObject) : LuaTable() {
        init {
            set("inline", LuaValue.valueOf(field.inline))
            set("name", LuaValue.valueOf(field.name ?: ""))
            set("value", LuaValue.valueOf(field.value ?: ""))
        }
    }
}