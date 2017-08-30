package glorantq.ramszesz.config

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.Charset

/**
 * Created by glorantq on 2017. 07. 23..
 */
class ConfigFile {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger("ConfigFile")

        fun create(guildId: String): ConfigFile {
            val configFile: File = File("./config/$guildId.json")
            logger.info("Loading file: ${configFile.absolutePath}")
            if(configFile.exists()) {
                val guildConfig: ConfigFile = Gson().fromJson(configFile.readText(Charset.forName("UTF-8")), ConfigFile::class.java)
                guildConfig.guildId = guildId
                logger.info("Config already exists, returning...")
                return guildConfig
            } else {
                val guildConfig: ConfigFile = ConfigFile()
                guildConfig.guildId = guildId
                logger.info("Creating new config...")

                val json: String = Gson().toJson(guildConfig)
                if(!configFile.parentFile.exists()) configFile.parentFile.mkdirs()
                configFile.createNewFile()
                configFile.writeText(json, Charset.forName("UTF-8"))
                return guildConfig
            }
        }
    }

    var guildId = ""

    @SerializedName("emojiNameAppend")
    var emojiNameAppend: Boolean = true
        set(value) {
            field = value
            save()
        }

    @SerializedName("deleteCommands")
    var deleteCommands: Boolean = false
        set(value) {
            field = value
            save()
        }

    @SerializedName("userRole")
    var userRole: Long = -1
        set(value) {
            field = value
            save()
        }

    @SerializedName("adminRole")
    var adminRole: Long = -1
        set(value) {
            field = value
            save()
        }

    @SerializedName("logModerations")
    var logModerations: Boolean = false
        set(value) {
            field = value
            save()
        }

    @SerializedName("modLogChannel")
    var modLogChannel: Long = -1
        set(value) {
            field = value
            save()
        }

    private fun save() {
        val configFile: File = File("./config/$guildId.json")
        val json: String = Gson().toJson(this)
        if(!configFile.parentFile.exists()) configFile.parentFile.mkdirs()
        configFile.createNewFile()
        configFile.writeText(json, Charset.forName("UTF-8"))

        logger.info("Saved config for $guildId!")
    }
}